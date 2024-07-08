package com.maohua.ware.dao;

import com.maohua.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author maohua
 * @email mhpan.tju@gmail.com
 * @date 2024-06-17 10:53:52
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
    //1. if there is no such item, new , otherwise, update


    void addStock(@Param("skuId") Long skuId,@Param("wareId")Long wareId, @Param("skuNum")Integer skuNum);

    Long getSkuStock(Long skuId);
}
