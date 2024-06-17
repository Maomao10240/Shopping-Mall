package com.maohua.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.maohua.common.utils.PageUtils;
import com.maohua.order.entity.OrderReturnApplyEntity;

import java.util.Map;

/**
 * 订单退货申请
 *
 * @author maohua
 * @email mhpan.tju@gmail.com
 * @date 2024-06-17 10:49:24
 */
public interface OrderReturnApplyService extends IService<OrderReturnApplyEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

