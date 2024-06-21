package com.maohua.thirdparty;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class ThirdPartyApplicationTests {


    @Test
    public void testUpload(){
        String accessKey = "my";
        String secretKey = "my";
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
    }

}
