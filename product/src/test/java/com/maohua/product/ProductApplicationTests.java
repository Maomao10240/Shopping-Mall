package com.maohua.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.maohua.product.entity.BrandEntity;
import com.maohua.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {

        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id",11));
        list.forEach(System.out::println);
        System.out.println("save successfully");
    }

}
