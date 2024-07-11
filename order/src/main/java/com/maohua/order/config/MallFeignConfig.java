package com.maohua.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class MallFeignConfig {
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                //拿到之前的request
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                System.out.println("cart线程...interceptor"+Thread.currentThread().getId());
                if(requestAttributes != null){
                    HttpServletRequest request = requestAttributes.getRequest();
                    //同步请求头，因为我们之前丢失了cookie
                    String cookie = request.getHeader("Cookie");
                    template.header("Cookie", cookie);
                }


            }
        };

    }
}
