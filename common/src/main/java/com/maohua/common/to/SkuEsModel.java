package com.maohua.common.to;

import java.math.BigDecimal;
import java.util.List;

public class SkuEsModel {
    private Long skuId;
    private String skuTitle;
    private Long spuId;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    private Boolean hasStock;
    private Long hotSore;
    private String brandImg;
    private List<Attr> attrs;
}
