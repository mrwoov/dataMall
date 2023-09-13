package com.example.datamall.controller;


import com.example.datamall.entity.Auth;
import com.example.datamall.entity.Role;
import com.example.datamall.entity.RoleToAuth;
import com.example.datamall.service.AuthService;
import com.example.datamall.service.RoleService;
import com.example.datamall.service.RoleToAuthService;
import com.example.datamall.service.UserBaseService;
import com.example.datamall.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 权限角色对应表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-06-04
 */
@RestController
@RequestMapping("/roleToAuths")
public class RoleToAuthController {
    @Resource
    private RoleToAuthService roleToAuthService;
    @Resource
    private UserBaseService userBaseService;
    @Resource
    private RoleService roleService;
    @Resource
    private AuthService authService;

    //新增或修改
    @PostMapping("/admin")
    public ResultData save(@RequestBody RoleToAuth roleToAuth, @RequestHeader("token") String token) {
        boolean admin = userBaseService.checkUserHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }

        Role role = roleService.getOneByOption("role_name", roleToAuth.getRoleName());
        roleToAuth.setRoleId(role.getId());

        Auth auth = authService.getOneByOption("name", roleToAuth.getAuthName());
        roleToAuth.setAuthId(auth.getId());
        System.out.println(roleToAuth.getAuthId());
        System.out.println(roleToAuth.getRoleId());
        boolean state = roleToAuthService.saveOrUpdate(roleToAuth);
        return ResultData.state(state);
    }

    //删除by id
    @DeleteMapping("/admin/{id}")
    public ResultData delete(@PathVariable Integer id, @RequestHeader("token") String token) {
        boolean admin = userBaseService.checkUserHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        boolean state = roleToAuthService.removeById(id);
        return ResultData.state(state);
    }

    //批量删除
    @PostMapping("/admin/del_batch")
    public ResultData deleteBatch(@RequestBody List<Integer> ids, @RequestHeader("token") String token) {
        boolean admin = userBaseService.checkUserHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        boolean state = roleToAuthService.removeByIds(ids);
        return ResultData.state(state);
    }

    //查找单个
    @GetMapping("/admin/{id}")
    public ResultData findOne(@PathVariable Integer id, @RequestHeader("token") String token) {
        boolean admin = userBaseService.checkUserHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        RoleToAuth roleToAuth = roleToAuthService.getById(id);
        Role role = roleService.getById(roleToAuth.getRoleId());
        roleToAuth.setRoleName(role.getRoleName());
        Auth auth = authService.getById(roleToAuth.getAuthId());
        roleToAuth.setAuthName(auth.getName());
        return ResultData.success(roleToAuth);
    }

    //分页查询
    @PostMapping("/admin/query")
    public ResultData findPage(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestHeader("token") String token, @RequestBody RoleToAuth roleToAuth) {
        boolean admin = userBaseService.checkUserHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        String roleName = roleToAuth.getRoleName();
        String authName = roleToAuth.getAuthName();
        return ResultData.success(roleToAuthService.queryRTAInfoPageByOption(pageSize, pageNum, roleName, authName));
    }
}

