package com.dataMall.adminCenter.controller;


import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.entity.Auth;
import com.dataMall.adminCenter.entity.Role;
import com.dataMall.adminCenter.service.AccountService;
import com.dataMall.adminCenter.service.AdminService;
import com.dataMall.adminCenter.service.RoleService;
import com.dataMall.adminCenter.service.RoleToAuthService;
import com.dataMall.adminCenter.vo.ResultData;
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
    public ResultData save(@RequestBody Role role) {
        boolean status = roleService.saveOrUpdate(role);
        List<Integer> ids = role.getAuthIds();
        boolean optionStatus = roleToAuthService.resetRoleAuth(role.getId(), ids);
        return ResultData.state(optionStatus && status);
    }

    //删除by id
    @DeleteMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public ResultData delete(@PathVariable Integer id) {
        boolean delRoleAuth = roleToAuthService.clearRoleAllAuth(id);
        boolean state = roleService.removeById(id);
        return ResultData.state(state && delRoleAuth);
    }

    //批量删除
    @PostMapping("/admin/del_batch")
    @AdminAuth(value = authPath)
    public ResultData deleteBatch(@RequestBody List<Integer> ids) {
        boolean state = roleService.removeByIds(ids);
        return ResultData.state(state);
    }

    //管理员查找单个角色权限列表
    @GetMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public ResultData findOne(@PathVariable Integer id) {
        Role role = roleService.getById(id);
        List<Auth> authList = roleToAuthService.getRoleAuthList(id);
        role.setAuthList(authList);
        return ResultData.success(role);
    }


    //管理员获取角色列表
    @GetMapping("/admin")
    @AdminAuth(value = authPath)
    public ResultData getList() {
        List<Role> list = roleService.list();
        return ResultData.success(list);
    }

    //管理员分页查询
    @PostMapping("admin/query")
    public ResultData findPage(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody Role role) {
        if (pageNum == null || pageSize == null) {
            return ResultData.fail("参数缺少");
        }
        String roleName = role.getRoleName();
        return ResultData.success(roleService.queryRoleInfoPageByOption(pageSize, pageNum, roleName));
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
}