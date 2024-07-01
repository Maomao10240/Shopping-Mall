package com.maohua.product.service.impl;

import com.alibaba.nacos.common.utils.JacksonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.maohua.common.utils.PageUtils;
import com.maohua.product.service.CategoryBrandRelationService;
import com.maohua.product.vo.Catelog2Vo;
import org.apache.tomcat.util.json.JSONParser;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maohua.common.utils.PageUtils;
import com.maohua.common.utils.Query;

import com.maohua.product.dao.CategoryDao;
import com.maohua.product.entity.CategoryEntity;
import com.maohua.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所有分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //组装父子结构
        //找出所有一级分类
        List<CategoryEntity> level1Menus = categoryEntities.stream().filter(categoryEntity ->
            categoryEntity.getParentCid() == 0
        ).map((menu)->{
            menu.setChildren(getChildrens(menu, categoryEntities));
            return menu;
        }).sorted((menu1, menu2)->{
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> list) {
        //TODO
        baseMapper.deleteBatchIds(list);
    }

//    @CacheEvict(value = "category", key = "'getLevel1Categorys'")
    @Caching(evict={ @CacheEvict(value = "category", key = "'getLevel1Categorys'"), @CacheEvict(value = "category", key = "'getCatalogJson'")})
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());

    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        return new Long[0];
    }

    //需要指定缓存的分区
    //默认expire -1，永不过期
    @Cacheable(value ={"category"}, key ="#root.methodName", sync = true) //当前结果需要缓存，如果缓存中有，方法不用调用
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    @Cacheable(value = {"category"}, key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        System.out.println("查询了数据库。。。。。。。。");
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //1.查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        //2.encapsulate
        Map<String, List<Catelog2Vo>> parent = level1Categorys.stream().collect(Collectors.toMap(k->k.getCatId().toString(), v->{
            //查到一级分类下的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if(categoryEntities!=null&&categoryEntities.size()>0){
                catelog2Vos = categoryEntities.stream().map(item ->{
                    Catelog2Vo vp = new Catelog2Vo(item.getCatId().toString(), null, item.getCatId().toString(), item.getName());
                    //找当前二级分类的三级分类
                    List<CategoryEntity> level3Vos = getParent_cid(selectList, item.getCatId());
                    if(level3Vos != null && level3Vos.size()>0){
                        List<Catelog2Vo.Catalog3Vo> collect = level3Vos.stream().map(l3->{
                            Catelog2Vo.Catalog3Vo catelog3Vo = new Catelog2Vo.Catalog3Vo(item.getCatId().toString(),l3.getCatId().toString(), l3.getName().toString());
                            return catelog3Vo;
                        }).collect(Collectors.toList());

                        vp.setCatalog3List(collect);
                    }
                    return vp;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        return parent;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        //cache save all json formate
        if(StringUtils.isEmpty(catalogJson)){
            Map<String, List<Catelog2Vo>> catelogJsonFromDb = getCatalogJsonFromDbWithRedisLock();
            //String s = JacksonUtils.toJson(catelogJsonFromDb);
            //redisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);
            return catelogJsonFromDb;
        }
        Map<String, List<Catelog2Vo>> result = null;
        try {
            result = new ObjectMapper().readValue(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ;
        return result;

    }
    //分布式锁
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        String lockKey = "lock:catalogJson-lock";
        RLock lock = redissonClient.getLock(lockKey);
        Map<String, List<Catelog2Vo>> result = null;
        lock.lock();
        try{
            result = getCatalogJsonFromDbWithLocalLock();
        }finally {
            lock.unlock();
        }
        return result;



/*
        //1.占分布式锁。



        //异常 死锁问题 ----设置过期时间



       // Boolean look = redisTemplate.opsForValue().setIfAbsent("lock", "111");



        String token = UUID.randomUUID().toString();



        Boolean look = redisTemplate.opsForValue().setIfAbsent("lock", token, 10, TimeUnit.MINUTES);



        Map<String, List<Catelog2Vo>> result = null;



        if(look){



            System.out.println("get the lock __________");



            try{



                //枷锁成功



                //    redisTemplate.expire("lock", 10, TimeUnit.MINUTES);



                result = getStringListMap();



                //保证删的自己的锁



//            String lockValue = redisTemplate.opsForValue().get("lock");



//            if(token.equals(lockValue)){



//                //返回的时候过期 怎么办



//                redisTemplate.delete("lock");



//            }



            }finally {



                String script = "if redis.call('get', KEY[1]) == ARGV[1] then return redis.call('del', KEY[1]) else return 0 end";



                redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), token);



            }



            return result;



        }else{
            //失败， 重试
            System.out.println("fail the lock __________");
            try{
                Thread.sleep(200);
            }catch (Exception e){

            }

            return getCatalogJsonFromDbWithRedisLock();
        } */

    }

    private Map<String, List<Catelog2Vo>> getStringListMap() {
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        if(!StringUtils.isEmpty(catalogJson))
        { System.out.println("缓存命中。。。。。。。。");
            Map<String, List<Catelog2Vo>> result = null;
            try {
                result = new ObjectMapper().readValue(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            ;
            return result;
        }
        System.out.println("查询了数据库。。。。。。。。"+Thread.currentThread().getId());
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //1.查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        //2.encapsulate
        Map<String, List<Catelog2Vo>> parent = level1Categorys.stream().collect(Collectors.toMap(k->k.getCatId().toString(), v->{
            //查到一级分类下的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if(categoryEntities!=null&&categoryEntities.size()>0){
                catelog2Vos = categoryEntities.stream().map(item ->{
                    Catelog2Vo vp = new Catelog2Vo(item.getCatId().toString(), null, item.getCatId().toString(), item.getName());
                    //找当前二级分类的三级分类
                    List<CategoryEntity> level3Vos = getParent_cid(selectList, item.getCatId());
                    if(level3Vos != null && level3Vos.size()>0){
                        List<Catelog2Vo.Catalog3Vo> collect = level3Vos.stream().map(l3->{
                            Catelog2Vo.Catalog3Vo catelog3Vo = new Catelog2Vo.Catalog3Vo(item.getCatId().toString(),l3.getCatId().toString(), l3.getName().toString());
                            return catelog3Vo;
                        }).collect(Collectors.toList());

                        vp.setCatalog3List(collect);
                    }
                    return vp;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        String s = JacksonUtils.toJson(parent);
        redisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);
        return parent;
    }


    //从数据库中查询
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        //SpringBoot所有的组件都是单例的

        //cache save all json formate
        //TODO 本地锁： synchronized, JUC(lock), 在分布式情况下，想要锁住所有，必须使用分布式锁

            synchronized (this){
                return getStringListMap();
            }





    }

    private List<CategoryEntity> getParent_cid( List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item->item.getParentCid().equals(parent_cid)).collect(Collectors.toList());
        //return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        return collect;
    }

    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //找子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2)->{
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }


}