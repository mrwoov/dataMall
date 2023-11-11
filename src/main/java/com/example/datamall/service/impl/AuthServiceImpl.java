package com.example.datamall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamall.entity.Auth;
import com.example.datamall.mapper.AuthMapper;
import com.example.datamall.service.AuthService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        return listToTree(auths);
    }

    @Override
    public List<Auth> listToTree(List<Auth> list) {
        Map<Integer, Auth> map = new HashMap<>();
        List<Auth> result = new ArrayList<>();

        // 首先将所有权限添加到映射中
        for (Auth auth : list) {
            map.put(auth.getId(), auth);
        }

        for (Auth auth : list) {
            if (auth.getParentId() == 0) {
                // 如果是一级权限，直接添加到结果列表
                result.add(auth);
            } else {
                // 查找父权限
                Auth parent = map.get(auth.getParentId());
                if (parent != null) {
                    // 如果父权限存在，添加为子权限
                    parent.addChild(auth);
                } else {
                    // 如果父权限不存在，将其父权限id设为-1，归为功能的父权限中
                    parent = new Auth();
                    parent.setId(-1);
                    parent.setIcon("el-icon-s-grid");
                    parent.setDescription("功能"); // 可以根据需要设置名称
                    parent.addChild(auth);
                    result.add(parent); // 将新创建的父权限添加到结果列表
                    map.put(parent.getId(), parent); // 更新映射
                }
            }
        }

        return result;
    }

    @Override
    public List<Auth> getChild(Integer parentId) {
        QueryWrapper<Auth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        return list(queryWrapper);
    }
}
