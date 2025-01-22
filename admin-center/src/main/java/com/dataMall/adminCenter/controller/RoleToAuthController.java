package com.dataMall.adminCenter.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.service.AuthService;
import com.dataMall.adminCenter.service.RoleService;
import com.dataMall.adminCenter.service.RoleToAuthService;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.Auth;
import com.dataMall.common.entity.Role;
import com.dataMall.common.entity.RoleToAuth;
import com.dataMall.common.exception.BusinessException;
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
    public BaseResponse<Object> save(@RequestBody RoleToAuth roleToAuth) {
        Role role = roleService.getOneByOption("role_name", roleToAuth.getRoleName());
        roleToAuth.setRoleId(role.getId());
        Auth auth = authService.getOneByOption("name", roleToAuth.getAuthName());
        roleToAuth.setAuthId(auth.getId());
        boolean state = roleToAuthService.saveOrUpdate(roleToAuth);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //删除by id
    @DeleteMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public BaseResponse<Object> delete(@PathVariable Integer id) {
        boolean state = roleToAuthService.removeById(id);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //批量删除
    @PostMapping("/admin/del_batch")
    @AdminAuth(value = authPath)
    public BaseResponse<Object> deleteBatch(@RequestBody List<Integer> ids) {
        boolean state = roleToAuthService.removeByIds(ids);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //查找单个
    @GetMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public BaseResponse<RoleToAuth> findOne(@PathVariable Integer id) {
        RoleToAuth roleToAuth = roleToAuthService.getById(id);
        Role role = roleService.getById(roleToAuth.getRoleId());
        roleToAuth.setRoleName(role.getRoleName());
        Auth auth = authService.getById(roleToAuth.getAuthId());
        roleToAuth.setAuthName(auth.getName());
        return ResultUtils.success(roleToAuth);
    }

    //分页查询
    @PostMapping("/admin/query")
    @AdminAuth(value = authPath)
    public BaseResponse<IPage<RoleToAuth>> findPage(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody RoleToAuth roleToAuth) {
        String roleName = roleToAuth.getRoleName();
        String authName = roleToAuth.getAuthName();
        return ResultUtils.success(roleToAuthService.queryRTAInfoPageByOption(pageSize, pageNum, roleName, authName));
    }
}

