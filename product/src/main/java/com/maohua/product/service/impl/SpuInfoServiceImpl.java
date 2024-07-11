package com.maohua.product.service.impl;

import com.maohua.common.constant.ProductConstant;
import com.maohua.common.to.SkuHasStockVo;
import com.maohua.common.to.SkuReductionTo;
import com.maohua.common.to.SpuBoundsTo;
import com.maohua.common.to.es.SkuEsModel;
import com.maohua.common.utils.PageUtils;
import com.maohua.common.utils.R;
import com.maohua.product.entity.*;
import com.maohua.product.feign.CouponFeignService;
import com.maohua.product.feign.SearchFeignService;
import com.maohua.product.feign.WareFeignService;
import com.maohua.product.service.*;
import com.maohua.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maohua.common.utils.Query;

import com.maohua.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService imagesService;
    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService attrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuInfo) {
        //save basic info : spu_ifo
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo, infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(infoEntity);
        //save spu desc images: spu_info_desc
        List<String> decript = spuInfo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSPuInfoDesc(descEntity);
        //save spu image collections: spu_images
        List<String> images = spuInfo.getImages();
        imagesService.saveImages(infoEntity.getId(), images);
        //save spu attrs : product_attr_value
        List<BaseAttrs> baseAttrs = spuInfo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr->{
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(attrEntity.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(infoEntity.getId());
            return valueEntity;
        }).collect(Collectors.toList());
        attrValueService.saveProductAttr(collect);
        //save sku info : pms_sku_info
        Bounds bounds = spuInfo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(infoEntity.getId());

        couponFeignService.saveSpuBounds(spuBoundsTo);



        List<Skus> skus = spuInfo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(item->{
                String defaultImg = "";
                for(Images image:item.getImages()){
                    if(image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img->{
                    SkuImagesEntity imagesEntity = new SkuImagesEntity();
                    imagesEntity.setSkuId(skuId);
                    imagesEntity.setImgUrl(img.getImgUrl());
                    imagesEntity.setDefaultImg(img.getDefaultImg());
                    return imagesEntity;
                }).filter(entity->{
                    //返回true 需要
                    return StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                //save spu 积分：mall_sms->sms_spu_bounds

                //1) sku basci: sku_info
                //2) sku images: sku_images
                //TODO :没有图片路劲的不用保存
                skuImagesService.saveBatch(imagesEntities);
                //3)sku sales attrs: sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a->{
                    SkuSaleAttrValueEntity saleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, saleAttrValueEntity);
                    saleAttrValueEntity.setSkuId(skuId);
                    return saleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                //4) sku coupon infor: mall_sms
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if(skuReductionTo.getFullCount() > 0 && skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) > 0){
                    R r =couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r.getCode()== 0){
                        log.error("save sku info error");
                    }
                }

            });
        }



    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        String staus = (String) params.get("status");
        if(!StringUtils.isEmpty(staus)){
            wrapper.eq("publish_status", 1);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&& !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id", brandId);
        }
        String catId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(brandId)&& !"0".equalsIgnoreCase(catId)){
            wrapper.eq("catalog_id", catId);
        }
        
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    //商品上架
    @Override
    public void up(Long spuId) {
        //组装ES需要的数据
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIdList = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        List<ProductAttrValueEntity> baseAttrs = attrValueService.baseAttrListforSpu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(attr->{
            return attr.getAttrId();
        }).collect(Collectors.toList());
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
        Set<Long> idSet = new HashSet<>(searchAttrIds);
       List<SkuEsModel.Attrs> attrsList = baseAttrs.stream().filter(item->{
           return idSet.contains(item.getAttrId());
        }).map(item->{
            SkuEsModel.Attrs attr1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attr1);
            return attr1;
        }).collect(Collectors.toList());
        Map<Long, Boolean> stockMap = null;
        try{
            List<SkuHasStockVo> skusHasStock = wareFeignService.getSkusHasStock(skuIdList);
            stockMap =skusHasStock.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item->item.getHasStock()));
        }catch (Exception e){
            System.out.println("remote ware access failed");
        }


        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> upProducts = skus.stream().map(sku->{
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            //TODO: SEARCH warehouse to see whether it has stock
            if(finalStockMap == null){
                esModel.setHasStock(true);
            }else{
                esModel.setHasStock(finalStockMap.get(sku.getSpuId()));
            }



            //TODO: hotsocore. default is 0
            esModel.setHotScore(0L);
            BrandEntity brandEntity = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brandEntity.getName());
            esModel.setBrandImg(brandEntity.getLogo());
            CategoryEntity categoryEntity = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(categoryEntity.getName());

            //TODO: search all sku attr values
            esModel.setAttrs(attrsList);
            return esModel;
        }).collect(Collectors.toList());

        //TODO: send all data to es
        R r= searchFeignService.productStatusUp(upProducts);
        if(r.getCode()==0){
            //TODO:x修改当前spu上架状态
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else{
            //TODO:重复调用，借口幂等性
        }


    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {

        SkuInfoEntity byId= skuInfoService.getById(skuId);
        Long spuId = byId.getSpuId();
        SpuInfoEntity spuInfoEntity = getById(spuId);
        return spuInfoEntity;
    }


}