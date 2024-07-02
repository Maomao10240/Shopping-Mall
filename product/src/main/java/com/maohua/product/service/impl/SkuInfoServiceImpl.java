package com.maohua.product.service.impl;

import com.maohua.common.utils.PageUtils;
import com.maohua.product.dao.SkuImagesDao;
import com.maohua.product.entity.SkuImagesEntity;
import com.maohua.product.entity.SpuInfoDescEntity;
import com.maohua.product.service.*;
import com.maohua.product.vo.SkuItemSaleAttrVo;
import com.maohua.product.vo.SkuItemVo;
import com.maohua.product.vo.SpuItemAttrGroupVo;
import org.checkerframework.checker.signature.qual.PolySignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maohua.common.utils.PageUtils;
import com.maohua.common.utils.Query;

import com.maohua.product.dao.SkuInfoDao;
import com.maohua.product.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;

@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuImagesDao skuImagesDao;
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    AttrGroupService attrGroupService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    ThreadPoolExecutor executor;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w->{
                w.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&& !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&& !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if(!StringUtils.isEmpty(min)){
           wrapper.ge("price", min);
        }
        String max = (String) params.get("max");

        if(!StringUtils.isEmpty(max)){
            try{
                BigDecimal bigDecimal = new BigDecimal(max);
                if(bigDecimal.compareTo(new BigDecimal("0"))==1) {
                    wrapper.le("price", max);
                }
            }catch(Exception e){
            }


        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();

       CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(()->{
           //1.sku 基本信息 pms_sku_info
            SkuInfoEntity skuInfoEntity = getById(skuId);
            skuItemVo.setSkuInfoEntity(skuInfoEntity);
            return skuInfoEntity;
            }, executor);
        CompletableFuture<Void> saleAttrFuture =infoFuture.thenAcceptAsync((res)->{
           //3. spu sales attr pms_spu
           //Long spuId = skuInfoEntity.getSpuId();
           List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
           skuItemVo.setSaleAttr(saleAttrVos);
       }, executor);
        CompletableFuture<Void> desFuture = infoFuture.thenAcceptAsync((res)->{
           //4. spu的介绍
           SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
           skuItemVo.setDes(spuInfoDescEntity);
       }, executor);
        CompletableFuture<Void> baseFuture = infoFuture.thenAcceptAsync((res)->{
            //5. spu 的规格参数信息
            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroupVos);
           // return skuItemVo;
        }, executor);

//        Long catalogId = skuInfoEntity.getCatalogId();
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(()->{
            //2, sku 图片信息 pms_sku_images
            List<SkuImagesEntity> images = skuImagesDao.selectList(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
            skuItemVo.setImages(images);

        }, executor);
        //等待所有任务完成
        try {
            CompletableFuture.allOf(infoFuture, saleAttrFuture, desFuture, baseFuture, imageFuture).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        return skuItemVo;



    }

}