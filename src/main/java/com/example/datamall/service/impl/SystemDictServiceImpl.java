package com.example.datamall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamall.entity.SystemDict;
import com.example.datamall.mapper.SystemDictMapper;
import com.example.datamall.service.SystemDictService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-10-07
 */
@Service
public class SystemDictServiceImpl extends ServiceImpl<SystemDictMapper, SystemDict> implements SystemDictService {

    @Override
    public List<SystemDict> getOneType(String type) {
        QueryWrapper<SystemDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        return list(queryWrapper);
    }
}
