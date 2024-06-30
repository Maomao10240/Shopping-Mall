package com.maohua.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MyRedissonConfig {
    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() throws IOException{
        //连接虚拟机
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.33.10:6379");
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    };
}
