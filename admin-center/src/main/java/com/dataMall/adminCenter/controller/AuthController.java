package com.dataMall.adminCenter.controller;

import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.entity.Auth;
import com.dataMall.adminCenter.service.AuthService;
import com.dataMall.adminCenter.service.SystemDictService;
import com.dataMall.adminCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


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
    public ResultData save(@RequestBody Auth auth) {
        boolean state = authService.saveOrUpdate(auth);
        return ResultData.state(state);
    }

    //管理员删除权限表中的权限
    @DeleteMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public ResultData delete(@PathVariable Integer id) {
        return ResultData.state(authService.del(id));
    }

    //管理员获取权限树
    @GetMapping("/admin")
    @AdminAuth(value = authPath)
    public ResultData getTree() {
        return ResultData.success(authService.getAuthTree());
    }

    //获取权限图标
    @GetMapping("/icons")
    public ResultData getIcons() {
        return ResultData.success(systemDictService.getOneType("icon"));
    }
}