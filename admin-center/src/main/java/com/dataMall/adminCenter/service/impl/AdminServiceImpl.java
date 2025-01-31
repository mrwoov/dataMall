package com.dataMall.adminCenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.adminCenter.mapper.AdminMapper;
import com.dataMall.adminCenter.service.AdminService;
import com.dataMall.adminCenter.service.RoleService;
import com.dataMall.adminCenter.service.UserService;
import com.dataMall.common.entity.Admin;
import com.dataMall.common.entity.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 管理员表 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-09-14
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Resource
    private RoleService roleService;
    @Resource
    private UserService userService;

    /**
     * 判断是否是管理员
     *
     * @param accountId 账户id
     * @return 是否是管理员
     */
    @Override
    public boolean isAdmin(Integer accountId) {
        Admin admin = getOneByOption("account_id", accountId);
        return admin != null;
    }


    //根据条件查询单个
    @Override
    public Admin getOneByOption(String column, Object value) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return getOne(queryWrapper);
    }

    @Override
    public IPage<Admin> query(String userName, Integer role, Integer pageNum, Integer pageSize) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        if (userName != null && !userName.isEmpty()) {
            User user = userService.getOneByOption("username", userName);
            if (user == null) {
                return null;
            }
            queryWrapper.like("account_id", user.getId());
        }
        if (role != null) {
            queryWrapper.like("role", role);
        }
        IPage<Admin> iPage = page(new Page<>(pageNum, pageSize), queryWrapper);
        for (Admin admin : iPage.getRecords()) {
            admin.setUsername(userService.getById(admin.getAccountId()).getUsername());
            admin.setRoleName(roleService.getById(admin.getRole()).getRoleName());
        }
        return iPage;
    }
}
