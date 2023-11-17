package com.dataMall.adminCenter.controller;

import com.dataMall.adminCenter.entity.Auth;
import com.dataMall.adminCenter.service.AccountService;
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
    private AccountService accountService;
    @Resource
    private SystemDictService systemDictService;
    private final String authPath = "auths";

    //管理员新增或修改权限
    @PatchMapping("/admin")
    public ResultData save(@RequestBody Auth auth, @RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        boolean state = authService.saveOrUpdate(auth);
        return ResultData.state(state);
    }

    //管理员删除权限表中的权限
    @DeleteMapping("/admin/{id}")
    public ResultData delete(@PathVariable Integer id, @RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        return ResultData.state(authService.del(id));
    }

    //管理员获取权限树
    @GetMapping("/admin")
    public ResultData getTree(@RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        return ResultData.success(authService.getAuthTree());
    }

    //获取权限图标
    @GetMapping("/icons")
    public ResultData getIcons() {
        return ResultData.success(systemDictService.getOneType("icon"));
    }
}