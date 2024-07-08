package com.maohua.member.service.impl;

import com.maohua.member.vo.MemberLoginVo;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maohua.common.utils.PageUtils;
import com.maohua.common.utils.Query;

import com.maohua.member.dao.MemberDao;
import com.maohua.member.entity.MemberEntity;
import com.maohua.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String username = vo.getUsername();
        String password = vo.getPassword();
        //去数据库 SELECT * FROM `ums_member` WHERE username =? O OR mobile =?
        MemberDao memberDao = this.baseMapper;
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("username", username).or().eq("mobile", username));
        if(memberEntity == null){
            return null;
        }else{
            return memberEntity;
        }

    }

}