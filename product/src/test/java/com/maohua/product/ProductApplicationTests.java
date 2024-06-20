package com.maohua.product;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.maohua.product.entity.BrandEntity;
import com.maohua.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.File;
import java.util.List;

@SpringBootTest
class ProductApplicationTests {

    @Autowired
    BrandService brandService;
    @Test
    public void testUpload(){
        String accessKey = "AKIA5FTZBT4SNYWCQOE7";
        String secretKey = "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";
        String bucketName = "mall-maohua";
        String filePath = "/Users/jiacong/Desktop/pg1.png";
        String keyName = "your-object-key";


        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion("us-east-2") // Change to your region
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();


        try {
            File file = new File(filePath);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, file);
            s3Client.putObject(putObjectRequest);
            System.out.println("File uploaded successfully.");
        } catch (Exception e) {
            System.out.println("File uploaded failed.");
        }

    }

    @Test
    void contextLoads() {

        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id",11));
        list.forEach(System.out::println);
        System.out.println("save successfully");
    }

}
