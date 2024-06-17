package com.maohua.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.maohua.common.utils.PageUtils;
import com.maohua.common.utils.PageUtils;
import com.maohua.product.entity.AttrGroupEntity;

import java.util.Map;

/**
 * 属性分组
 *
 * @author maohua
 * @email mhpan.tju@gmail.com
 * @date 2024-06-16 15:55:21
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

