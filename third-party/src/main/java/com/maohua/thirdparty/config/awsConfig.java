package com.maohua.thirdparty.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class awsConfig {

    @Value("${amazon.aws.accessKey}")
    private String accessKey;

    @Value("${amazon.aws.secretKey}")
    private String secretKey;

    @Value("${amazon.aws.region}")
    private String regionName;

    @Bean
    public AmazonS3 getS3Client(){
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return   AmazonS3ClientBuilder.standard()
                .withRegion(regionName) // Change to your region
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

}
