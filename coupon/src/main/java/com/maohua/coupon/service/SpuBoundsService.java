package com.maohua.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.maohua.common.utils.PageUtils;
import com.maohua.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author maohua
 * @email mhpan.tju@gmail.com
 * @date 2024-06-17 10:23:36
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

