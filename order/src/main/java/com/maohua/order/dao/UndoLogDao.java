package com.maohua.order.dao;

import com.maohua.order.entity.UndoLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author maohua
 * @email mhpan.tju@gmail.com
 * @date 2024-06-17 10:49:24
 */
@Mapper
public interface UndoLogDao extends BaseMapper<UndoLogEntity> {
	
}
