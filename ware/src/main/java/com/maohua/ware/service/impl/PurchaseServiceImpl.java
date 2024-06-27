package com.maohua.ware.service.impl;

import com.maohua.common.constant.WareConstant;
import com.maohua.ware.entity.PurchaseDetailEntity;
import com.maohua.ware.service.PurchaseDetailService;
import com.maohua.ware.service.WareSkuService;
import com.maohua.ware.vo.MergeVo;
import com.maohua.ware.vo.PurchaseFinishVo;
import com.maohua.ware.vo.PurchaseItemFinishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maohua.common.utils.PageUtils;
import com.maohua.common.utils.Query;

import com.maohua.ware.dao.PurchaseDao;
import com.maohua.ware.entity.PurchaseEntity;
import com.maohua.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

import static javax.management.Query.or;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;
    @Autowired
    WareSkuService wareSkuService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).or().eq("status", 0);
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
               wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            //TODO 确认采购单状态是0 or 1
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatus.CREATED.getCode());
            this.save(purchaseEntity);

        }
        List<Long> items = mergeVo.getItems();
        List<PurchaseDetailEntity> collect = items.stream().map(i->{
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(i);
            detailEntity.setPurchaseId(purchaseId);
            detailEntity.setStatus(WareConstant.PurchaseDetailStatus.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

    }

    @Override
    public void received(List<Long> ids) {
        //make sure whether the ticket is new or assigned
        List<PurchaseEntity> collect = ids.stream().map(id->{
            PurchaseEntity purchaseEntity = this.getById(id);
            return purchaseEntity;
        }).filter(item->{
            if(item.getStatus() == WareConstant.PurchaseStatus.CREATED.getCode() ||item.getStatus() == WareConstant.PurchaseStatus.ASSIGNED.getCode()){
                return true;
            }
            return false;
        }).map(item->{
            item.setStatus(WareConstant.PurchaseStatus.RECEIVED.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        //change the ticket condition
        this.updateBatchById(collect);

        //change
        collect.forEach(item->{
            List<PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurchasedId(item.getId());
            List<PurchaseDetailEntity> detailEntities = entities.stream().map(entitiy->{
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                detailEntity.setId(entitiy.getId());
                detailEntity.setStatus(WareConstant.PurchaseDetailStatus.BUYING.getCode());

                return detailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntities);
        });
    }
    @Transactional
    @Override
    public void done(PurchaseFinishVo doneVo) {
        //1. change ticket status
        Long id = doneVo.getId();

        Boolean flag = true;
        List<PurchaseItemFinishVO> items = doneVo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        if(items == null){
            for(PurchaseItemFinishVO item:items){
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();

                if(item.getStatus() == WareConstant.PurchaseStatus.HASERROR.getCode()){
                    flag = false;
                    detailEntity.setStatus(item.getStatus());
                }else{
                    detailEntity.setStatus(item.getStatus());
                    //入库
                    PurchaseDetailEntity entity = purchaseDetailService.getById(item.getId());
                    wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());
                }
                detailEntity.setId(item.getId());
                updates.add(detailEntity);

            }
        }

        purchaseDetailService.updateBatchById(updates);
        //2. 采购项目状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setUpdateTime(new Date());
        purchaseEntity.setStatus(flag? WareConstant.PurchaseDetailStatus.FINISH.getCode() : WareConstant.PurchaseStatus.HASERROR.getCode());
        this.updateById(purchaseEntity);
        //3. 入库

    }

}