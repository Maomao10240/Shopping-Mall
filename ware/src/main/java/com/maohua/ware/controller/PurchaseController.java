package com.maohua.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.maohua.ware.vo.MergeVo;
import com.maohua.ware.vo.PurchaseFinishVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.maohua.ware.entity.PurchaseEntity;
import com.maohua.ware.service.PurchaseService;
import com.maohua.common.utils.PageUtils;
import com.maohua.common.utils.R;



/**
 * 采购信息
 *
 * @author maohua
 * @email mhpan.tju@gmail.com
 * @date 2024-06-17 10:53:52
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    //领取采购单
    @PostMapping("/received")
    public R received(@RequestBody List<Long> ids) {
        purchaseService.received(ids);
        return R.ok();
    }

    @PostMapping("/done")
    public R finish(@RequestBody PurchaseFinishVo doneVo) {
        purchaseService.done(doneVo);
        return R.ok();
    }

    @PostMapping("/merge")
    public R merge(@RequestBody MergeVo mergeVo) {
        purchaseService.mergePurchase(mergeVo);
        return R.ok();
    }
    @RequestMapping("/list")
    //@RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }
    @GetMapping("/unreceive/list")
    //@RequiresPermissions("ware:purchase:list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceive(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setUpdateTime(new Date());
        purchase.setCreateTime(new Date());
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
