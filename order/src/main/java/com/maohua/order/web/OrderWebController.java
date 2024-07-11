package com.maohua.order.web;

import com.maohua.order.service.OrderService;
import com.maohua.order.vo.OrderConfirmVo;
import com.maohua.order.vo.OrderSubmitVo;
import com.maohua.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {
    @Autowired
    OrderService orderService;


    @GetMapping("/toTrade")
    public String toTrade(Model model, HttpServletRequest request) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", confirmVo);
        return "confirm";
    }
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model){

        System.out.println("订单提交的数据。。。"+vo);
        SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);
        if(responseVo.getCode() == 0){
            model.addAttribute("submitOrderResp", responseVo);
            return "pay";
        }else{
            return "redirect:http://order.mall.com/toTrade";
        }


    }

}
