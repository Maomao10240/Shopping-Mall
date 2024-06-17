package com.maohua.coupon.dao;

import com.maohua.coupon.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author maohua
 * @email mhpan.tju@gmail.com
 * @date 2024-06-17 10:23:36
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {
	
}
