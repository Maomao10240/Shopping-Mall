package com.maohua.authserver.feign;

import com.maohua.authserver.vo.UserLoginVo;
import com.maohua.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("member")
public interface MemberFeignService {

    @PostMapping("member/member/login")
    public R login(@RequestBody UserLoginVo vo);
}
