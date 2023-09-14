package com.example.datamall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.datamall.entity.Admin;
import com.example.datamall.mapper.AdminMapper;
import com.example.datamall.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

    @Override
    public boolean isAdmin(Integer accountId) {
        Admin admin = getOneByOption("account_id",accountId);
        return admin != null;
    }


    @Override
    public Admin getOneByOption(String column, Object value){
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column,value);
        return getOne(queryWrapper);
    }
}
