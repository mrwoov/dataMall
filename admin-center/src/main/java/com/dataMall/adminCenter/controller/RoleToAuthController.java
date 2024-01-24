package com.dataMall.adminCenter.controller;


import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.entity.Auth;
import com.dataMall.adminCenter.entity.Role;
import com.dataMall.adminCenter.entity.RoleToAuth;
import com.dataMall.adminCenter.service.AuthService;
import com.dataMall.adminCenter.service.RoleService;
import com.dataMall.adminCenter.service.RoleToAuthService;
import com.dataMall.adminCenter.vo.ResultData;
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
    private final String authPath = "roles";
    @Resource
    private RoleToAuthService roleToAuthService;
    @Resource
    private RoleService roleService;
    @Resource
    private AuthService authService;

    //新增或修改
    @PostMapping("/admin")
    @AdminAuth(value = authPath)
    public ResultData save(@RequestBody RoleToAuth roleToAuth) {
        Role role = roleService.getOneByOption("role_name", roleToAuth.getRoleName());
        roleToAuth.setRoleId(role.getId());
        Auth auth = authService.getOneByOption("name", roleToAuth.getAuthName());
        roleToAuth.setAuthId(auth.getId());
        boolean state = roleToAuthService.saveOrUpdate(roleToAuth);
        return ResultData.state(state);
    }

    //删除by id
    @DeleteMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public ResultData delete(@PathVariable Integer id) {
        boolean state = roleToAuthService.removeById(id);
        return ResultData.state(state);
    }

    //批量删除
    @PostMapping("/admin/del_batch")
    @AdminAuth(value = authPath)
    public ResultData deleteBatch(@RequestBody List<Integer> ids) {
        boolean state = roleToAuthService.removeByIds(ids);
        return ResultData.state(state);
    }

    //查找单个
    @GetMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public ResultData findOne(@PathVariable Integer id) {
        RoleToAuth roleToAuth = roleToAuthService.getById(id);
        Role role = roleService.getById(roleToAuth.getRoleId());
        roleToAuth.setRoleName(role.getRoleName());
        Auth auth = authService.getById(roleToAuth.getAuthId());
        roleToAuth.setAuthName(auth.getName());
        return ResultData.success(roleToAuth);
    }

    //分页查询
    @PostMapping("/admin/query")
    @AdminAuth(value = authPath)
    public ResultData findPage(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody RoleToAuth roleToAuth) {
        String roleName = roleToAuth.getRoleName();
        String authName = roleToAuth.getAuthName();
        return ResultData.success(roleToAuthService.queryRTAInfoPageByOption(pageSize, pageNum, roleName, authName));
    }
}

