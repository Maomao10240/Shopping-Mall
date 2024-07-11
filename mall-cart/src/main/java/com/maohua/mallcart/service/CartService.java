package com.maohua.mallcart.service;

import com.maohua.mallcart.vo.Cart;
import com.maohua.mallcart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartService {
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItem getCartItem(Long skuId);

    Cart getCart();

    void changeItemCount(Long skuId, Long num);

    void deleteItem(Long skuId);

    List<CartItem> getUserCartItems();
}
