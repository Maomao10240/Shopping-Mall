package com.maohua.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.maohua.common.utils.PageUtils;
import com.maohua.common.utils.PageUtils;
import com.maohua.product.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author maohua
 * @email mhpan.tju@gmail.com
 * @date 2024-06-16 15:55:21
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

