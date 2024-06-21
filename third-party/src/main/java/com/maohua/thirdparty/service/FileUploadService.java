package com.maohua.thirdparty.service;


import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class FileUploadService {

    @Autowired
    private AmazonS3 s3Client;



    public String generatePreSignUrl(String filePath, String bucketName, HttpMethod httpMethod) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 10);//GIVE 10 MIN
        return s3Client.generatePresignedUrl(bucketName, filePath, calendar.getTime(), httpMethod).toString();
    }



}
