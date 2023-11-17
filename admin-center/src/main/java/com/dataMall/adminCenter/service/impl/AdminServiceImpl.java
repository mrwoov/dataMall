package com.dataMall.adminCenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.adminCenter.entity.Admin;
import com.dataMall.adminCenter.mapper.AdminMapper;
import com.dataMall.adminCenter.service.AdminService;
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

    //判断是否为管理员
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
}
