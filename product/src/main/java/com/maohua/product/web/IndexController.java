package com.maohua.product.web;

import com.maohua.product.entity.CategoryEntity;
import com.maohua.product.service.CategoryService;
import com.maohua.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redissonClient;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model){
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/json/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson(){
        Map<String,List<Catelog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        String lockKey = "lock:hello";  // Use a namespaced key
        RLock lock = redissonClient.getLock(lockKey);
        System.out.println(".............");
        lock.lock();
        try{
            System.out.println("jiasuo success......."+ Thread.currentThread().getId());
            Thread.sleep(3000);
        }catch(Exception e){

        }finally {
            System.out.println("release lock success......."+ Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
//        try {
//            // Try to acquire the lock within 5 seconds and set a lease time of 10 seconds
//            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
//                try {
//                    System.out.println("Lock acquired successfully by thread: " + Thread.currentThread().getId());
//                    // Simulate some work with the lock
//                    Thread.sleep(3000);
//                } finally {
//                    lock.unlock();
//                    System.out.println("Lock released by thread: " + Thread.currentThread().getId());
//                }
//            } else {
//                System.out.println("Failed to acquire lock by thread: " + Thread.currentThread().getId());
//            }
//        } catch (InterruptedException e) {
//            System.err.println("Thread interrupted: " + e.getMessage());
//            Thread.currentThread().interrupt(); // Restore interrupted status
//        } catch (Exception e) {
//            System.err.println("An error occurred: " + e.getMessage());
//        }
//        return "hello";

    }
}
