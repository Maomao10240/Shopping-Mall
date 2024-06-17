package com.maohua.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.maohua.common.utils.PageUtils;
import com.maohua.order.entity.PaymentInfoEntity;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author maohua
 * @email mhpan.tju@gmail.com
 * @date 2024-06-17 10:49:24
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

