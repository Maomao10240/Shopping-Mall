package com.maohua.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParam {

    private String keyword;
    private Long category3Id;
    //sort = saleCount_asc/dsc
    //sort = skuPrice/ hotScore
    private String sort;

    //filter: hasStock, skuPrice range, brandid， attrs
    private Integer hasStock;
    private String skuPrice;
    private List<Long> brandIds;//multiple choice
    //attrs = 2_5寸： 6寸
    private List<String> attrs;
    private Integer pageNum;

}
