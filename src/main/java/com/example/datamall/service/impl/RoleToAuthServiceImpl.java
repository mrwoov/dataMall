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
}
