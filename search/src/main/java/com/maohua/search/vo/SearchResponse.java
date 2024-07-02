package com.maohua.search.vo;

import com.maohua.common.to.SkuEsModel;
import lombok.Data;

import java.util.List;

@Data
public class SearchResponse {
    private List<SkuEsModel> products;
    private Integer pageNum;
    private Long total;
    private Integer totalPages;
    private List<BrandVo> brands;
    private List<AttrVo> attrs;//当前查询到的结果所涉及的所有属性
    private List<CatalogVo> catalogs;

    @Data
    public static class BrandVo{
        private Integer brandId;
        private String brandName;
        private String brandImg;
    }
    @Data
    public static class AttrVo{
        private Integer attrId;
        private String attrName;
        private List<String> attrValue;
    }
    @Data
    public static class CatalogVo{
        private Integer catalogId;
        private String catalogName;

    }
}
