package com.maohua.order.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class OrderConfirmVo {
    //1.收货地址列表
    List<MemberAddressVo> address;
    //2.所有选中购物商品
    List<OrderItemVo> items;
    //优惠劵
    Integer integeration;
    //防止重复
    String orderToken;
    Integer count;
    BigDecimal total;
    BigDecimal payPrice;

    Map<Long, Boolean> stocks;

    public Integer getCount(){
        Integer i = 0;
        if(items != null){
            for(OrderItemVo item: items){
                i += item.getCount();
            }
        }
        return i;
    }

//    BigDecimal total;
   // BigDecimal payPrice;
    public BigDecimal getTotal(){
        BigDecimal sum = new BigDecimal("0");
        if(items != null){
            for(OrderItemVo item:items){
                BigDecimal data = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum = sum.add(data);
            }
        }
        return sum;
    }
    public BigDecimal getPayPrice(){
       return getTotal();
    }


}
