package com.dataMall.adminCenter.aop.aspect;

import com.alibaba.fastjson.JSON;
import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.service.AccountService;
import com.dataMall.adminCenter.vo.ResultData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AdminAuthAspect {

    @Autowired
    private AccountService accountService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Around("@annotation(adminAuth)")
    public Object checkAdminAuth(ProceedingJoinPoint joinPoint,AdminAuth adminAuth) throws Throwable {
        // 从注解中获取权限路径
        String authPath = adminAuth.value();
        // 从请求头中获取token
        String token = request.getHeader("token");
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            // 没有权限时直接返回响应给前端
            response.setContentType("application/json;charset=utf-8");
            ResultData resultData = ResultData.fail("无权限");
            //将ResultData对象转换成json字符串
            String resultDataJson = JSON.toJSONString(resultData);
            response.getWriter().write(resultDataJson); // 这里可以根据需要修改返回的内容
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
            return null; // 返回null表示切面不再继续执行原有方法
        }

        // 有权限时继续执行原有方法
        return joinPoint.proceed();
    }
}
