package com.dataMall.adminCenter.controller;

import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.service.AuthService;
import com.dataMall.adminCenter.service.SystemDictService;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.Auth;
import com.dataMall.common.entity.SystemDict;
import com.dataMall.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 * 权限表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-06-04
 */
@RestController
@RequestMapping("/auths")
public class AuthController {
    @Resource
    private AuthService authService;
    @Resource
    private SystemDictService systemDictService;
    private final String authPath = "auths";

    //管理员新增或修改权限
    @PatchMapping("/admin")
    @AdminAuth(value = authPath)
    public BaseResponse<Object> save(@RequestBody Auth auth) {
        boolean state = authService.saveOrUpdate(auth);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //管理员删除权限表中的权限
    @DeleteMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public BaseResponse<Object> delete(@PathVariable Integer id) {
        boolean state = authService.del(id);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        return ResultUtils.success();
    }

    //管理员获取权限树
    @GetMapping("/admin")
    @AdminAuth(value = authPath)
    public BaseResponse<List<Auth>> getTree() {
        return ResultUtils.success(authService.getAuthTree());
    }

    //获取权限图标
    @GetMapping("/icons")
    public BaseResponse<List<SystemDict>> getIcons() {
        return ResultUtils.success(systemDictService.getOneType("icon"));
    }
}