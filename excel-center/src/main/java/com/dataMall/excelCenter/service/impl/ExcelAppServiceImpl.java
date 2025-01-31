package com.dataMall.excelCenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.common.entity.ExcelApp;
import com.dataMall.common.enums.ExcelAppStateTypeEnum;
import com.dataMall.excelCenter.mapper.ExcelAppMapper;
import com.dataMall.excelCenter.service.ExcelAppService;
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

    @Override
    public boolean checkCreateAuth(Integer uid, String appId) {
        QueryWrapper<ExcelApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id",appId);
        queryWrapper.eq("uid",uid);
        queryWrapper.eq("states", ExcelAppStateTypeEnum.ONLY_UPLOAD.getValue());
        return count(queryWrapper) == 1;
    }

    @Override
    public boolean checkAppId(String appId) {
        QueryWrapper<ExcelApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id",appId);
        return count(queryWrapper) == 1;
    }
    
    
}
