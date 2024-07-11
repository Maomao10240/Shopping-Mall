package com.maohua.order.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Data
public class OrderSubmitVo {
    private Long addrId;
    private Integer payType;
    //无需提交购买的商品，去购物车获取
    //优惠 发票
    private String orderToken;
    private BigDecimal payPrice;//检验价格
    //用户相关信息在session
    private String note;
}
