package com.maohua.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.maohua.common.utils.PageUtils;
import com.maohua.member.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author maohua
 * @email mhpan.tju@gmail.com
 * @date 2024-06-17 10:40:00
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

