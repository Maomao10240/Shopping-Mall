package com.maohua.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuEsModel {
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private String skuDesc;
    private Long saleCount;
    private Long brandId;
    private String brandName;
    private Boolean hasStock;
    private Long hotScore;
    private Long catalogId;
    private String catalogName;
    private String brandImg;
    private List<Attrs> attrs;
    @Data
    public static class Attrs{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }

}
//     "mappings":{
//        "properties": {
//            "skuId":{
//                "type":"long"
//            },
//            "spuId":{
//                "type":"keyword"
//            },
//            "skuTitle":{
//                "type":"text",
//                        "analyzer": "standard"
//            },
//            "skuPrice":{
//                "type":"keyword"
//            },
//            "skuImg":{
//                "type":"keyword",
//                        "index":false,
//                        "doc_values": false
//            },
//            "saleCount":{
//                "type":"long"
//            }
//            "hasStock":{
//                "type":"boolean"
//            },
//            "hotScore":{
//                "type":"long"
//            },
//            "brandId":{
//                "type":"long"
//            },
//            "catalogId":{
//                "type":"long"
//            },
//            "brandName":{
//                "type":"keyword",
//                        "index":false,
//                        "doc_values": false
//            },
//            "brandImg":{
//                "type":"keyword",
//                        "index":false,
//                        "doc_values": false
//            },
//            "catalogName":{
//                "type":"keyword",
//                        "index":false,
//                        "doc_values": false
//            },
//            "attrs":{
//                "type":"nested",
//                        "properties": {
//                    "attrId":{
//                        "type":"long"
//                    },
//                    "attrName":{
//                        "type":"keyword",
//                                "index":false,
//                                "doc_values": false
//                    },
//                    "attrValue":{
//                        "type":"keyword"
//                    }
//                }
//            },
//
//        }
