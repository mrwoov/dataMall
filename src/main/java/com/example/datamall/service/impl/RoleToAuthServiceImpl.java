package com.example.datamall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamall.entity.Auth;
import com.example.datamall.entity.Role;
import com.example.datamall.entity.RoleToAuth;
import com.example.datamall.mapper.RoleToAuthMapper;
import com.example.datamall.service.AuthService;
import com.example.datamall.service.RoleService;
import com.example.datamall.service.RoleToAuthService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 权限角色对应表 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-06-04
 */
@Service
public class RoleToAuthServiceImpl extends ServiceImpl<RoleToAuthMapper, RoleToAuth> implements RoleToAuthService {
    @Resource
    private RoleService roleService;
    @Resource
    private AuthService authService;

    @Override
    public RoleToAuth getOneByOption(String column, Object value) {
        QueryWrapper<RoleToAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return getOne(queryWrapper);
    }

    @Override
    public List<Auth> getRoleAuthList(Integer roleId) {
        QueryWrapper<RoleToAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        List<RoleToAuth> list = list(queryWrapper);
        List<Auth> authList = new ArrayList<>();
        for (RoleToAuth roleToAuth : list) {
            Auth auth = authService.getById(roleToAuth.getAuthId());
            authList.add(auth);
        }
        return authList;
    }

    @Override
    public List<Auth> getRoleAuths(Integer roleId) {
        List<Auth> authList = getRoleAuthList(roleId);
        return authService.listToTree(authList);
    }

    @Override
    public IPage<RoleToAuth> queryRTAInfoPageByOption(Integer pageSize, Integer pageNum, String roleName, String authName) {
        QueryWrapper<RoleToAuth> roleToAuthQueryWrapper = new QueryWrapper<>();

        if (roleName != null && !roleName.isEmpty()) {
            QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.like("role_name", roleName);
            List<Role> roleList = roleService.list(roleQueryWrapper);
            for (Role tempRole : roleList) {
                roleToAuthQueryWrapper.eq("role_id", tempRole.getId()).or();
            }
        }
        if (authName != null && !authName.isEmpty()) {
            QueryWrapper<Auth> authQueryWrapper = new QueryWrapper<>();
            authQueryWrapper.like("name", authName);
            List<Auth> authList = authService.list(authQueryWrapper);
            for (Auth tempAuth : authList) {
                roleToAuthQueryWrapper.eq("auth_id", tempAuth.getId());
            }
        }
        IPage<RoleToAuth> page = page(new Page<>(pageNum, pageSize), roleToAuthQueryWrapper);
        for (RoleToAuth tempRoleToAuth : page.getRecords()) {
            Role role = roleService.getById(tempRoleToAuth.getRoleId());
            tempRoleToAuth.setRoleName(role.getRoleName());
            Auth auth = authService.getById(tempRoleToAuth.getAuthId());
            tempRoleToAuth.setAuthName(auth.getName());
        }
        return page;
    }

    @Override
    public boolean resetRoleAuth(Integer roleId, List<Integer> auths) {
        clearRoleAllAuth(roleId);
        int flag = 0;
        for (int i : auths) {
            RoleToAuth roleToAuth = new RoleToAuth();
            roleToAuth.setRoleId(roleId);
            roleToAuth.setAuthId(i);
            boolean status = save(roleToAuth);
            if (!status) flag++;
        }
        return flag == 0;
    }

    @Override
    public boolean clearRoleAllAuth(Integer roleId) {
        QueryWrapper<RoleToAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        return remove(queryWrapper);
    }
}
