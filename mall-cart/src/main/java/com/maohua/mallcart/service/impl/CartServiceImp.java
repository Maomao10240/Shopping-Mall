package com.maohua.mallcart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.maohua.common.utils.R;
import com.maohua.mallcart.feign.ProductFeignService;
import com.maohua.mallcart.interceptor.CartInterceptor;
import com.maohua.mallcart.service.CartService;
import com.maohua.mallcart.vo.Cart;
import com.maohua.mallcart.vo.CartItem;
import com.maohua.mallcart.vo.SkuInfoVo;
import com.maohua.mallcart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImp implements CartService {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    ThreadPoolExecutor executor;

    private final String CART_PREFIX = "mall:cart";

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {

        //1.获取我们要操作的购物车
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String res = (String) cartOps.get(skuId.toString());
        if(StringUtils.isEmpty(res)){
            CartItem cartItem = new CartItem();
            //2. 远程查询商品信息
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(()->{
                R skuInfo = productFeignService.getSkuInfo(skuId);
                SkuInfoVo skuInfoVo = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>(){});
                //3.商品添加到购物车

                cartItem.setCheck(true);
                cartItem.setSkuId(skuId);
                cartItem.setCount(1);
                cartItem.setTitle(skuInfoVo.getSkuTitle());
                cartItem.setImage(skuInfoVo.getSkuDefaultImg());
                cartItem.setPrice(skuInfoVo.getPrice());
            }, executor);
            //4.远程查询attr信息
            CompletableFuture getSkuSaleAttrValues = CompletableFuture.runAsync(()->{
                List<String> values =productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(values);
            }, executor);
            CompletableFuture.allOf(getSkuInfoTask, getSkuSaleAttrValues).get();
            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), s);
            return cartItem;
        }else{
            //有此商品，修改数量
            CartItem item = JSON.parseObject(res, CartItem.class);
            item.setCount(item.getCount()+1);
            String s = JSON.toJSONString(item);
            cartOps.put(skuId.toString(), s);
            return item;
        }





    }

    @Override
    public CartItem getCartItem(Long skuId) {
        //1.获取我们要操作的购物车
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String res = (String) cartOps.get(skuId.toString());
        CartItem item = JSON.parseObject(res, CartItem.class);
        return item;
    }

    @Override
    public Cart getCart() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Cart cart = new Cart();
        if(userInfoTo.getUserId() != null){
            //登录状态
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            List<CartItem>  cartList = getCartItems(cartKey);
            cart.setItems(cartList);
            return cart;
        }else{
            return cart;
        }

    }

    @Override
    public void changeItemCount(Long skuId, Long num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(Math.toIntExact(num));
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));

    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(userInfoTo.getUserId() == null){
            return null;
        }else{
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(cartKey);
            //获取选定的
            List<CartItem> collect = cartItems.stream().filter(item->item.getCheck()).map(item->{
                //更新到现在价格
                R cur = productFeignService.getPrice(item.getSkuId());
                String data = (String) cur.get("data");
                item.setPrice(new BigDecimal(data));
                return item;
            }).collect(Collectors.toList());

            return collect;
        }
    }

    private List<CartItem>  getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if(values!= null && values.size()>0){
            List<CartItem> collect = values.stream().map(obj->{
                String str = (String) obj;
                CartItem cartItem =JSON.parseObject(str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if(userInfoTo.getUserId() != null){
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        }else{
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }
}
