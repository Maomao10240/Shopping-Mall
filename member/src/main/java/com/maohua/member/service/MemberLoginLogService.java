package com.maohua.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.maohua.common.utils.PageUtils;
import com.maohua.member.entity.MemberLoginLogEntity;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author maohua
 * @email mhpan.tju@gmail.com
 * @date 2024-06-17 10:40:00
 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

