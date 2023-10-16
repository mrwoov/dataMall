package com.example.datamall.controller;


import com.example.datamall.entity.Auth;
import com.example.datamall.entity.Role;
import com.example.datamall.service.AccountService;
import com.example.datamall.service.AdminService;
import com.example.datamall.service.RoleService;
import com.example.datamall.service.RoleToAuthService;
import com.example.datamall.vo.ResultData;
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
    public ResultData save(@RequestBody Role role, @RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        boolean status = roleService.saveOrUpdate(role);
        List<Integer> ids = role.getAuthIds();
        boolean optionStatus = roleToAuthService.resetRoleAuth(role.getId(), ids);
        return ResultData.state(optionStatus && status);
    }

    //删除by id
    @DeleteMapping("/admin/{id}")
    public ResultData delete(@PathVariable Integer id, @RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        boolean delRoleAuth = roleToAuthService.clearRoleAllAuth(id);
        boolean state = roleService.removeById(id);
        return ResultData.state(state && delRoleAuth);
    }

    //批量删除
    @PostMapping("/admin/del_batch")
    public ResultData deleteBatch(@RequestBody List<Integer> ids, @RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        boolean state = roleService.removeByIds(ids);
        return ResultData.state(state);
    }

    //管理员查找单个角色权限列表
    @GetMapping("/admin/{id}")
    public ResultData findOne(@PathVariable Integer id, @RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        Role role = roleService.getById(id);
        List<Auth> authList = roleToAuthService.getRoleAuthList(id);
        role.setAuthList(authList);
        return ResultData.success(role);
    }

    //用户查询权限树
    @GetMapping("/getAuths")
    public ResultData getAuths(@RequestHeader("token") String token) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            return ResultData.fail("登陆过期");
        }
        Integer roleId = adminService.getOneByOption("account_id", uid).getRole();
        List<Auth> authList = roleToAuthService.getRoleAuths(roleId);
        return ResultData.success(authList);
    }

    //用户查询权限列表
    @GetMapping("/getAuthList")
    public ResultData getAuthList(@RequestHeader("token") String token) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            return ResultData.fail("登陆过期");
        }
        Integer roleId = adminService.getOneByOption("account_id", uid).getRole();
        return ResultData.success(roleToAuthService.getRoleAuthList(roleId));
    }

    //获取角色列表
    @GetMapping("/admin")
    public ResultData getList(@RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        List<Role> list = roleService.list();
        return ResultData.success(list);
    }

    //分页查询
    @PostMapping("admin/query")
    public ResultData findPage(@RequestHeader("token") String token, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody Role role) {
        if (pageNum == null || pageSize == null) {
            return ResultData.fail("参数缺少");
        }
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        String roleName = role.getRoleName();
        return ResultData.success(roleService.queryRoleInfoPageByOption(pageSize, pageNum, roleName));
    }
}