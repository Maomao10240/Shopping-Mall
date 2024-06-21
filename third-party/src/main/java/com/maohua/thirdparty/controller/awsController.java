package com.maohua.thirdparty.controller;

import com.amazonaws.HttpMethod;
import com.maohua.thirdparty.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;


@RestController
public class awsController {
    @Autowired
    FileUploadService fileUploadService;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @RequestMapping("/aws/policy")
    public ResponseEntity<String> policy(@RequestParam String extension){
        return ResponseEntity.ok(
                fileUploadService.generatePreSignUrl(UUID.randomUUID()+"."+extension, bucket, HttpMethod.PUT)
        );
    }
}
