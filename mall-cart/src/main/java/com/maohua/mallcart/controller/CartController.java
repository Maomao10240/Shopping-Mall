package com.maohua.mallcart.controller;

import com.maohua.mallcart.interceptor.CartInterceptor;
import com.maohua.mallcart.service.CartService;
import com.maohua.mallcart.vo.Cart;
import com.maohua.mallcart.vo.CartItem;
import com.maohua.mallcart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {
    @Autowired
    CartService cartService;
//
//    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("check") Integer check){
//
//    }
    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItem> getCurrentCartItem(){
        return cartService.getUserCartItems();
    }


    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Long num){
        cartService.changeItemCount(skuId, num);
        return "redirect:http://cart.mall.com/cart.html";
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.mall.com/cart.html";
    }

    @GetMapping("/cart.html")
    public String cartListPage(Model model){
        //登录有userid, 没登录有user-key / 先去interceptor
        //threadlocal 好处是线程结束后就没有了， session 占空间
//        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
 //       System.out.println(userInfoTo);
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);

        return "cartList";
    }
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num")Integer num, RedirectAttributes ra) throws ExecutionException, InterruptedException {
        //RedirectAttributes ra
        // ra.addFlashAttribute() 将数据放到session 里面，只能取一次
        CartItem cartItem = cartService.addToCart(skuId, num);
//        model.addAttribute("item", cartItem);
        ra.addAttribute("skuId", skuId);
        return "redirect:http://cart.mall.com/addToCartSuccess.html";
    }
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccess(@RequestParam("skuId") Long skuId, Model model){
        CartItem item = cartService.getCartItem(skuId);
        model.addAttribute("item", item);
        return "success";
    }


}
