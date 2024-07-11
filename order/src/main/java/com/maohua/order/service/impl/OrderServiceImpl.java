package com.maohua.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.maohua.common.exception.NoStockException;
import com.maohua.common.utils.R;
import com.maohua.common.vo.MemberRespVo;
import com.maohua.order.constant.OrderConstant;
import com.maohua.order.entity.OrderItemEntity;
import com.maohua.order.enume.OrderStatusEnum;
import com.maohua.order.feign.CartFeignService;
import com.maohua.order.feign.MemberFeignService;
import com.maohua.order.feign.ProductFeignService;
import com.maohua.order.feign.WmsFeignService;
import com.maohua.order.interceptor.LoginUserInterceptor;
import com.maohua.order.service.OrderItemService;
import com.maohua.order.to.OrderCreateTo;
import com.maohua.order.vo.*;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maohua.common.utils.PageUtils;
import com.maohua.common.utils.Query;

import com.maohua.order.dao.OrderDao;
import com.maohua.order.entity.OrderEntity;
import com.maohua.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    private ThreadLocal<OrderSubmitVo> voThreadLocal = new ThreadLocal<>();
    @Autowired
    OrderItemService orderItemService;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    WmsFeignService wmsFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.threadLocal.get();
        System.out.println("cart线程 mem..."+Thread.currentThread().getId());
        //获取之前的请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> getAddress = CompletableFuture.runAsync(()->{
            //1.查会员信息
            //每次都用之前的请求
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(address);
            System.out.println("cart线程...add"+Thread.currentThread().getId());

        }, executor);
        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(()->{
            RequestContextHolder.setRequestAttributes(requestAttributes);

            List<OrderItemVo> items = cartFeignService.getCurrentCartItem();
            confirmVo.setItems(items);
            System.out.println("cart线程...cart"+Thread.currentThread().getId());

        }, executor).thenRunAsync(()->{
            RequestContextHolder.setRequestAttributes(requestAttributes);
            System.out.println("cart线程...ware"+Thread.currentThread().getId());
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> collect = items.stream().map(OrderItemVo::getSkuId
            ).toList();
            List<SkuStockVo> hasStock = wmsFeignService.getSkusHasStock(collect);
            if(hasStock != null){
                Map<Long, Boolean> map = hasStock.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(map);
            }
        }, executor);

        //2.远程查询购物车

        //查询用户积分
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegeration(integration);


        //防止重复
        String token = UUID.randomUUID().toString().replace("-","");
        //服务器
        confirmVo.setOrderToken(token);
        //redis
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+ memberRespVo.getId(), token, 30, TimeUnit.MINUTES);

        CompletableFuture.allOf(getAddress, cartFuture).get();
        return confirmVo;
    }

    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        voThreadLocal.set(vo);
        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.threadLocal.get();
        response.setCode(0);
        //1.验证令牌
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();
        //atomatic 0 ordertoken failed, 1 delete token success
        String redisToken = redisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberRespVo.getId());
        Long result =redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberRespVo.getId()), orderToken);
        if(result == 1L){
            //创建订单
            OrderCreateTo order = createOrder();
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if(Math.abs(payAmount.subtract(payPrice).doubleValue()) <0.01){
                //保存订单
                saveOrder(order);
                //锁定库存,只要有异常，回滚
                //1.订单号 skuid, qty
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> collect = order.getOrderItems().stream().map(item->{
                    OrderItemVo itemVo = new OrderItemVo();
                    itemVo.setCount(item.getSkuQuantity());
                    itemVo.setTitle(item.getSkuName());
                    itemVo.setSkuId(item.getSkuId());
                    return itemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(collect);
                R r = wmsFeignService.orderLockStock(lockVo);
                if(r.getCode() ==0){
                    //锁成功
                    response.setOrder(order.getOrder());
                    return response;
                }else{
                    throw new NoStockException(order.getOrder().getId());
//                    response.setCode(3);
//                    return response;
                }
            }else{
                response.setCode(2);
                return response;
            }
        }else{
            response.setCode(1);
            return response;
        }
    }

    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);
        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems);



    }

    private OrderCreateTo createOrder(){
        MemberRespVo respVo = LoginUserInterceptor.threadLocal.get();
        OrderCreateTo createTo = new OrderCreateTo();
        String orderSn = IdWorker.getTimeId();
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(orderSn);
        entity.setMemberId(respVo.getId());
        OrderSubmitVo submitVo = voThreadLocal.get();
        //获取收货地址
        Long addrId= submitVo.getAddrId();

        R fare = wmsFeignService.getFare((addrId));
        FareVo fareResp = fare.getData(new TypeReference<FareVo>(){});
        entity.setFreightAmount(fareResp.getFare());
        entity.setReceiverProvince(fareResp.getAddress().getProvince());
        entity.setReceiverName(fareResp.getAddress().getName());
        entity.setReceiverCity(fareResp.getAddress().getCity());
        entity.setReceiverPostCode(fareResp.getAddress().getPostCode());
        //设置订单状态
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        //获取所有订单
        List<OrderItemEntity> itemEmtities = buildOrderItems(orderSn);
        //检验价格
        computePrice(entity, itemEmtities);




        createTo.setOrder(entity);
        createTo.setOrderItems(itemEmtities);
        return createTo;



    }

    private void computePrice(OrderEntity entity, List<OrderItemEntity> itemEmtities) {
        BigDecimal total = new BigDecimal("0");
        for(OrderItemEntity item: itemEmtities){
            BigDecimal realAmount = item.getRealAmount();
            total= total.add(realAmount);
        }
        //1. 订单里的价格
        entity.setTotalAmount(total);
        entity.setPayAmount(total.add(entity.getFreightAmount()));

    }

    private  List<OrderItemEntity> buildOrderItems(String orderSn) {
        List<OrderItemVo> currentCart = cartFeignService.getCurrentCartItem();
        if(currentCart != null && currentCart.size() > 0){
            List<OrderItemEntity> itemEntities = currentCart.stream().map(cartItem->{
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
            return itemEntities;
        }
        return null;
    }

    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        //1.订单信息
        //2.spu信息
        Long skuId = cartItem.getSkuId();
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>(){});
        itemEntity.setSpuId(data.getId());
        itemEntity.setSpuBrand(data.getBrandId().toString());
        itemEntity.setSpuName(data.getSpuName());
        itemEntity.setCategoryId(data.getCatalogId());

        //3.sku信息
        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());
        itemEntity.setSkuPrice(cartItem.getPrice());
        String skuAttr = String.join( ";", cartItem.getSkuAttr());
        itemEntity.setSkuAttrsVals(skuAttr);
        itemEntity.setSkuQuantity(cartItem.getCount());
        BigDecimal origin = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        itemEntity.setRealAmount(origin);
        //4. 积分信息
        itemEntity.setGiftGrowth(cartItem.getPrice().intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().intValue());
        return itemEntity;
    }

}