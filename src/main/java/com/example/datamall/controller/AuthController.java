package com.example.datamall.controller;

import com.example.datamall.entity.Auth;
import com.example.datamall.service.AccountService;
import com.example.datamall.service.AuthService;
import com.example.datamall.vo.ResultData;
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

    /**
     * description:新增或修改权限
     *
     * @param auth:具有auth的entity的json  post请求
     * @param token:具有/admin权限的用户token
     * @return com.example.datamall.vo.DataView
     * @author woov
     * @create 2023/6/17
     **/
    @PatchMapping("/admin")
    public ResultData save(@RequestBody Auth auth, @RequestHeader("token") String token) {
        boolean admin = accountService.checkUserHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        boolean state = authService.saveOrUpdate(auth);
        return ResultData.state(state);
    }

    /**
     * description:删除权限表中的权限
     *
     * @param id:权限id
     * @param token:具有/admin权限的用户token
     * @return com.example.datamall.vo.DataView
     * @author woov
     * @create 2023/6/17
     **/
    @DeleteMapping("/admin/{id}")
    public ResultData delete(@PathVariable Integer id, @RequestHeader("token") String token) {
        boolean admin = accountService.checkUserHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        boolean state = authService.removeById(id);
        boolean childState = authService.delChildId(id);
        return ResultData.state(state && childState);
    }

    @GetMapping("/admin")
    public ResultData getTree(@RequestHeader("token") String token) {
        boolean admin = accountService.checkUserHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        return ResultData.success(authService.getAuthTree());
    }
}