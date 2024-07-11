package com.maohua.order.to;

import com.maohua.order.entity.OrderEntity;
import com.maohua.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
    private BigDecimal payPrice;
    private BigDecimal fare;
}
