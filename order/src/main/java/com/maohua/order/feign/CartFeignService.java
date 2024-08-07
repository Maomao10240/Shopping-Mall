package com.maohua.order.feign;

import com.maohua.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
@FeignClient("mall-cart")
public interface CartFeignService {
    @GetMapping("/currentUserCartItems")
    public List<OrderItemVo> getCurrentCartItem();
}
