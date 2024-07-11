package com.maohua.ware.service.impl;

import com.maohua.common.exception.NoStockException;
import com.maohua.common.utils.R;
import com.maohua.ware.feign.ProductFeignService;
import com.maohua.ware.vo.OrderItemVo;
import com.maohua.ware.vo.SkuHasStockVo;
import com.maohua.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maohua.common.utils.PageUtils;
import com.maohua.common.utils.Query;

import com.maohua.ware.dao.WareSkuDao;
import com.maohua.ware.entity.WareSkuEntity;
import com.maohua.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    WareSkuDao wareSkuDao;
    @Autowired
    ProductFeignService productFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1. if there is no such item, new , otherwise, update
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(entities == null || entities.size() == 0){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //TODO search sku name, if failed, not necessary to rollback
            //catch exception
            try{
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                if(info.getCode() == 0){
                    wareSkuEntity.setSkuName(data.get("skuName").toString());
                }
            }catch(Exception e){

            }


            wareSkuDao.insert(wareSkuEntity);
        }else{
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {
       List<SkuHasStockVo> collect = skuIds.stream().map(skuId->{
            SkuHasStockVo vo = new SkuHasStockVo();
            Long count = baseMapper.getSkuStock(skuId);
            vo.setHasStock(count==null?false:count>0);
            vo.setSkuId(skuId);
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }

    @Transactional//默认运行时异常都roll back
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        //1.按照收货地址，找到最近的库存
        //1.找到商品在那个仓库有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item->{
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //查询skuid在哪里有库存
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());
        Boolean allLock = true;
        for(SkuWareHasStock hasStock: collect){
            Boolean skuStocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();

            if(wareIds == null || wareIds.size() == 0){
                throw new NoStockException(skuId);
            }
            for(Long wareId: wareIds){
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if(count == 0){
                    //当前仓库失败
                }else{
                    skuStocked = true;
                    break;
                }
            }
            if(!skuStocked){
                //当前商品所有仓库都没锁住
                throw new NoStockException(skuId);
            }
        }
        //肯定锁定成功了





        return true;
    }

    @Data
    class SkuWareHasStock{
        private Long skuId;
        private List<Long> wareId;
        private Integer num;

    }

}