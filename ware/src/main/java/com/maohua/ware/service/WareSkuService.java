package com.maohua.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.maohua.common.utils.PageUtils;
import com.maohua.ware.entity.WareSkuEntity;

import java.util.Map;

/**
 * 商品库存
 *
 * @author maohua
 * @email mhpan.tju@gmail.com
 * @date 2024-06-17 10:53:52
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

