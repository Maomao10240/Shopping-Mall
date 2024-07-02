package com.maohua.product.vo;

import com.maohua.product.entity.SkuImagesEntity;
import com.maohua.product.entity.SkuInfoEntity;
import com.maohua.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
public class SkuItemVo {
    //1.sku 基本信息 pms_sku_info
    SkuInfoEntity skuInfoEntity;
    boolean hasStock  = true;
    //2, sku 图片信息 pms_sku_images
    List<SkuImagesEntity> images;
    //3. spu sales attr pms_spu
    List<SkuItemSaleAttrVo> saleAttr;
    //4. spu的介绍
    SpuInfoDescEntity des;
    //5. spu 的规格参数信息
    List<SpuItemAttrGroupVo>  groupAttrs;



}
