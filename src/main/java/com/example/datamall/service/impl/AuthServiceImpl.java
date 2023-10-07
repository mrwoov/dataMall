package com.example.datamall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamall.entity.Auth;
import com.example.datamall.mapper.AuthMapper;
import com.example.datamall.service.AuthService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-06-04
 */
@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper, Auth> implements AuthService {
    @Override
    public Auth getOneByOption(String column, Object value) {
        QueryWrapper<Auth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return getOne(queryWrapper);
    }

    @Override
    public Boolean del(Integer id) {
        //先删子权限，再删一级
        boolean childState = delChildId(id);
        boolean state = removeById(id);
        return state && childState;
    }

    @Override
    public Boolean delChildId(Integer pid) {
        if (pid == null) {
            return false;
        }
        QueryWrapper<Auth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", pid);
        remove(queryWrapper);
        return true;
    }

    @Override
    public List<Auth> getAuthTree() {
        List<Auth> auths = list();
        List<Auth> result = new ArrayList<>();
        for (Auth parent : auths) {
            if (parent.getParentId() == 0) {
                result.add(parent);
            }
            for (Auth child : auths) {
                if ((parent.getId().equals(child.getParentId()))) {
                    parent.addChild(child);
                }
            }
        }
        return result;
    }
}
