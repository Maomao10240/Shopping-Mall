package com.maohua.product;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

//thymeleaf-starter 关闭缓存
//静态资源在static文件夹； 页面在templates 默认找index
//页面修改实时更新1. 引入dev-tools 2. control shift f9
@EnableFeignClients(basePackages = "com.maohua.product.feign")
@EnableDiscoveryClient
@MapperScan("com.maohua.product.dao")
@SpringBootApplication
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

}
