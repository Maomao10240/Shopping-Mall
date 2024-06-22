package com.maohua.thirdparty.controller;

import com.amazonaws.HttpMethod;
import com.maohua.common.utils.R;
import com.maohua.thirdparty.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
public class awsController {
    @Autowired
    FileUploadService fileUploadService;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${amazon.aws.accessKey}")
    private String accessId;
    @Value("${amazon.aws.secretKey}")
    private String accessKey;

    String endpoint = "s3.us-east-2.amazonaws.com";

    @RequestMapping("/aws/policy")
    public R policy(){

       String postSignature = fileUploadService.generatePreSignUrl(UUID.randomUUID()+"", bucket, HttpMethod.PUT);
       Map<String, String> respMap = new HashMap<>();
       respMap.put("signature", postSignature);
       String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
       String dir = format +"/";
       String host = "https://" + bucket +"."+endpoint;
       respMap.put("host", host);
       respMap.put("dir", dir);
       respMap.put("policy" , postSignature);
       respMap.put("accessId", accessId);
       return R.ok().put("data", respMap);
    }
}
