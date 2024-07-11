package com.maohua.mallcart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


public class Cart {
    List<CartItem> items;
    private Integer countNum;//total count
    private Integer countType;
    private BigDecimal totalAmount;
    private BigDecimal reduce = new BigDecimal("0");

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
//        this.getTotalAmount();
//        this.getCountNum();
//        this.getCountType();
    }

    public Integer getCountNum() {
        int count = 0;
        if(items!= null && items.size()>0){
            for(CartItem item: items){
                if(item.getCheck()){
                    countNum += item.getCount();
                }
            }
        }
        return count;
    }



    public Integer getCountType() {
        int count = 0;
        if(items!= null && items.size()>0){
            for(CartItem item: items){
                if(item.getCheck()){
                    countNum += 1;
                }
            }
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        //计算购物总价
        if(items!= null && items.size()>0){
            for(CartItem item: items){
                if(item.getCheck()){
                    BigDecimal totalPrice = item.getTotalPrice();
                    amount.add(totalPrice);
                }
            }
        }
        //减去优惠
        BigDecimal subtract = amount.subtract(getReduce());
        return subtract;
    }



    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
