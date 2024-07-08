package com.maohua.order.interceptor;

import com.maohua.common.constant.AuthServerConst;
import com.maohua.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberRespVo> threadLocal = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MemberRespVo attribute= (MemberRespVo)request.getSession().getAttribute(AuthServerConst.LOGIN_USER);
        if(attribute!=null){
            threadLocal.set(attribute);
            return true;
        }else{
            request.getSession().setAttribute("msg", "Please login first");
            response.sendRedirect("http://auth.mall.com/login.html");
            return false;
        }
    }
}
