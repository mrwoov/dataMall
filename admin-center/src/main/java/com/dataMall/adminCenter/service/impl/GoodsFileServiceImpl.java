package com.dataMall.adminCenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.adminCenter.entity.GoodsFile;
import com.dataMall.adminCenter.mapper.GoodsFileMapper;
import com.dataMall.adminCenter.service.GoodsFileService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品数据文件 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-10-26
 */
@Service
public class GoodsFileServiceImpl extends ServiceImpl<GoodsFileMapper, GoodsFile> implements GoodsFileService {

    @Override
    public GoodsFile getOneByOption(String column, Object value) {
        QueryWrapper<GoodsFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return getOne(queryWrapper);
    }
}
