package com.dataMall.adminCenter.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.common.BaseResponse;
import com.dataMall.adminCenter.common.ErrorCode;
import com.dataMall.adminCenter.entity.Auth;
import com.dataMall.adminCenter.entity.Role;
import com.dataMall.adminCenter.exception.BusinessException;
import com.dataMall.adminCenter.service.AccountService;
import com.dataMall.adminCenter.service.AdminService;
import com.dataMall.adminCenter.service.RoleService;
import com.dataMall.adminCenter.service.RoleToAuthService;
import com.dataMall.adminCenter.common.ResultUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-06-04
 */
@RestController
@RequestMapping("/roles")
public class RoleController {
    private final String authPath = "roles";
    @Resource
    private RoleService roleService;
    @Resource
    private AccountService accountService;
    @Resource
    private RoleToAuthService roleToAuthService;
    @Resource
    private AdminService adminService;

    //新增或修改
    @PatchMapping("/admin")
    @AdminAuth(value = authPath)
    public BaseResponse<Object> save(@RequestBody Role role) {
        boolean status = roleService.saveOrUpdate(role);
        List<Integer> ids = role.getAuthIds();
        boolean optionStatus = roleToAuthService.resetRoleAuth(role.getId(), ids);
        boolean state = optionStatus && status;
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //删除by id
    @DeleteMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public BaseResponse<Object> delete(@PathVariable Integer id) {
        boolean delRoleAuth = roleToAuthService.clearRoleAllAuth(id);
        boolean state = roleService.removeById(id);
        if (!(state && delRoleAuth)) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //批量删除
    @PostMapping("/admin/del_batch")
    @AdminAuth(value = authPath)
    public BaseResponse<Object> deleteBatch(@RequestBody List<Integer> ids) {
        boolean state = roleService.removeByIds(ids);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //管理员查找单个角色权限列表
    @GetMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public BaseResponse<Role> findOne(@PathVariable Integer id) {
        Role role = roleService.getById(id);
        List<Auth> authList = roleToAuthService.getRoleAuthList(id);
        role.setAuthList(authList);
        return ResultUtils.success(role);
    }


    //管理员获取角色列表
    @GetMapping("/admin")
    @AdminAuth(value = authPath)
    public BaseResponse<List<Role>> getList() {
        List<Role> list = roleService.list();
        return ResultUtils.success(list);
    }

    //管理员分页查询
    @PostMapping("admin/query")
    public BaseResponse<IPage<Role>> findPage(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody Role role) {
        if (pageNum == null || pageSize == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String roleName = role.getRoleName();
        return ResultUtils.success(roleService.queryRoleInfoPageByOption(pageSize, pageNum, roleName));
    }
    //用户查询权限列表
    @GetMapping("/getAuthList")
    public BaseResponse<List<Auth>> getAuthList(@RequestHeader("token") String token) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Integer roleId = adminService.getOneByOption("account_id", uid).getRole();
        return ResultUtils.success(roleToAuthService.getRoleAuthList(roleId));
    }

    @GetMapping("/getAuths")
    public BaseResponse<List<Auth>> getAuths(@RequestHeader("token") String token) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Integer roleId = adminService.getOneByOption("account_id", uid).getRole();
        List<Auth> authList = roleToAuthService.getRoleAuths(roleId);
        return ResultUtils.success(authList);
    }
}