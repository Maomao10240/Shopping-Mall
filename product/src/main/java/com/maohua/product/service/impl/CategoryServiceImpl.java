package com.maohua.product.service.impl;

import com.maohua.common.utils.PageUtils;
import com.maohua.product.entity.CategoryBrandRelationEntity;
import com.maohua.product.service.CategoryBrandRelationService;
import com.maohua.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
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

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        
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