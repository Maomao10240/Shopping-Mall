package com.maohua.ware.feign;

import com.maohua.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("product")
public interface ProductFeignService {
    //1.让所有request过gate  /api/product/skuinfo/info/{skuId}
    //2. 直接让后台指定服务处理 /product/skuinfo/info/{skuId}
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
