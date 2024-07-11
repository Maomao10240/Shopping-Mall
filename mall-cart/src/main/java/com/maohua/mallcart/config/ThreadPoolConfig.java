package com.maohua.mallcart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix ="mall.thread")
@Data
public class ThreadPoolConfig {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
