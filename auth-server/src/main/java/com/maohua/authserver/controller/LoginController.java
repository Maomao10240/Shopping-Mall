package com.maohua.authserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login.html")
    public String LoginPage(){
        return "login";
    }
    @GetMapping("/register.html")
    public String RegisterPage(){
        return "Register";
    }


}