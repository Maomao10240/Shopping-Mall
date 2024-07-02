package com.maohua.search.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maohua.search.vo.AttrEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品属性
 * 
 * @author maohua
 * @email mhpan.tju@gmail.com
 * @date 2024-06-16 15:55:21
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
	
}
