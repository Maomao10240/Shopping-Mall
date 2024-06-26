package com.maohua.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private int reduceCount;
    private BigDecimal discount;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int countStatus;
    private List<MemberPrice> memberPrices;
}
