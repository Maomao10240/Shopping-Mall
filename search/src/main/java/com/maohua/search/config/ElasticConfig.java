package com.maohua.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticConfig {

    @Bean
    public RestHighLevelClient esRestClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.33.10", 9200, "http")));
        return client;
    }

    public static final RequestOptions COMMON_OPTIONS;
    static{
        RequestOptions.Builder builder= RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }
}
