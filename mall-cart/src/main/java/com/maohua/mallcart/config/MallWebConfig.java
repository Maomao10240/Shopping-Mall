package com.maohua.mallcart.config;

import com.maohua.mallcart.interceptor.CartInterceptor;
import com.maohua.mallcart.vo.Cart;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MallWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");


    }
}
