package com.maohua.product;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


//
//@Cacheable: Triggers cache population. 出发数据保存
//
//@CacheEvict: Triggers cache eviction.
//
//@CachePut: Updates the cache without interfering with the method execution.
//
//@Caching: Regroups multiple cache operations to be applied on a method.
//
//@CacheConfig: Shares some common cache-related settings at class-level.


//thymeleaf-starter 关闭缓存
//静态资源在static文件夹； 页面在templates 默认找index
//页面修改实时更新1. 引入dev-tools 2. control shift f9
//@EnableCaching
@EnableFeignClients(basePackages = "com.maohua.product.feign")
@EnableDiscoveryClient
@MapperScan("com.maohua.product.dao")
@SpringBootApplication
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

}
