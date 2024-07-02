package com.maohua.product;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.maohua.product.dao.AttrGroupDao;
import com.maohua.product.dao.SkuSaleAttrValueDao;
import com.maohua.product.entity.BrandEntity;
import com.maohua.product.service.BrandService;
import com.maohua.product.vo.SkuItemSaleAttrVo;
import com.maohua.product.vo.SpuItemAttrGroupVo;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;


import java.io.File;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class ProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Test
    public void redisson(){
        System.out.println(redissonClient);
    }

    @Test
    public void test0(){
//        List<SpuItemAttrGroupVo> result = attrGroupDao.getAttrGroupWithAttrsBySpuId(13L, 225L);
//        System.out.println("size: "+ result.size() +" " + result.toString());
        List<SkuItemSaleAttrVo> result = skuSaleAttrValueDao.getSaleAttrsBySpuId(13L);
        System.out.println(result);

    }

    @Test
    public void testRedis(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello", "world_" + UUID.randomUUID().toString());
        System.out.println("data saved " + ops.get("hello"));
    }
    @Test
    void contextLoads() {

        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id",11));
        list.forEach(System.out::println);
        System.out.println("save successfully");
    }

}
