package com.maohua.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.maohua.common.utils.PageUtils;
import com.maohua.coupon.entity.HomeAdvEntity;

import java.util.Map;

/**
 * 首页轮播广告
 *
 * @author maohua
 * @email mhpan.tju@gmail.com
 * @date 2024-06-17 10:23:36
 */
public interface HomeAdvService extends IService<HomeAdvEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

