package com.maohua.common.exception;

public class NoStockException extends RuntimeException{
    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    private Long skuId;
    public NoStockException(long skuId){
        super("skuId: " + skuId +"not enough stock");
    }
}
