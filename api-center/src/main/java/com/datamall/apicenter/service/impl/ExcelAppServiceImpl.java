package com.dataMall.apicenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.common.entity.ExcelApp;
import com.dataMall.apicenter.mapper.ExcelAppMapper;
import com.dataMall.apicenter.service.ExcelAppService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author woov
 * @since 2024-04-02
 */
@Service
public class ExcelAppServiceImpl extends ServiceImpl<ExcelAppMapper, ExcelApp> implements ExcelAppService {

    @Override
    public ExcelApp getOneByOption(String colum, String value) {
        QueryWrapper<ExcelApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(colum,value);
        return getOne(queryWrapper);
    }

    @Override
    public boolean removeByAppId(String appId) {
        QueryWrapper<ExcelApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id",appId);
        return remove(queryWrapper);
    }
}
