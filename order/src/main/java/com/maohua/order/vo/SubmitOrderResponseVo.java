package com.maohua.order.vo;

import com.maohua.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code;

}
