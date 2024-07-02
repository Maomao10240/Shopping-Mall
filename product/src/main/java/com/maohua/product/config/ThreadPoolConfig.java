package com.maohua.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix ="mall.thread")
@Data
public class ThreadPoolConfig {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
