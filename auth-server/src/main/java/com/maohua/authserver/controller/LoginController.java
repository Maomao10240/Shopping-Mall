package com.maohua.authserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.maohua.authserver.feign.MemberFeignService;
import com.maohua.common.vo.MemberRespVo;
import com.maohua.authserver.vo.UserLoginVo;
import com.maohua.common.constant.AuthServerConst;
import com.maohua.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    MemberFeignService memberFeignService;

//    @GetMapping("/login.html")
//    public String LoginPage(){
//        return "login";
//    }
//    @GetMapping("/register.html")
//    public String RegisterPage(){
//        return "Register";
//    }
    @PostMapping("/register")
    public String register(){

        return "redirect:/login.html";
    }
    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttr, HttpSession session){
        System.out.println("lllllllll");
        R login = memberFeignService.login(vo);
        if(login.getCode() == 0){
            MemberRespVo data = login.getData("data", new TypeReference<MemberRespVo>(){});
            System.out.println("Login successful: user : "+ data.toString());
            session.setAttribute(AuthServerConst.LOGIN_USER, data);
//            System.out.println(session.loginUser);
            return "redirect:http://mall.com";
        }else{
            Map<String, String> errors = new HashMap<>();
           // errors.put("msg", login.getDat)
            System.out.println("Login Failed: user ");

            redirectAttr.addFlashAttribute("errors", "Failed");
            return "redirect:http://auth.mall.com/login.html";
        }



    }


}
