package com.maohua.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.maohua.common.utils.R;
import com.maohua.ware.feign.MemberFeignService;
import com.maohua.ware.vo.FareVo;
import com.maohua.ware.vo.MemberAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maohua.common.utils.PageUtils;
import com.maohua.common.utils.Query;

import com.maohua.ware.dao.WareInfoDao;
import com.maohua.ware.entity.WareInfoEntity;
import com.maohua.ware.service.WareInfoService;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {
    @Autowired
    MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(key)){
            wrapper.eq("id", key).or().like("name", key).or().like("address", key).or().like("areacode", key);
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long id) {
        FareVo fareVo = new FareVo();
        R r = memberFeignService.addrInfo(id);
        MemberAddressVo data = r.getData("memberReceiveAddress",new TypeReference<MemberAddressVo>(){});
        if(data!=null){
            String phone = data.getPhone();
           String substring = phone.substring(phone.length()-1, phone.length());
           BigDecimal bigDecimal = new BigDecimal(substring);
           fareVo.setAddress(data);
           fareVo.setFare(bigDecimal);

        }
        return fareVo;
    }

}