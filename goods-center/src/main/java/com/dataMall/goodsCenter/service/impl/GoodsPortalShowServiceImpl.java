package com.dataMall.goodsCenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.goodsCenter.entity.GoodsPortalShow;
import com.dataMall.goodsCenter.mapper.GoodsPortalShowMapper;
import com.dataMall.goodsCenter.service.GoodsPortalShowService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-11-23
 */
@Service
public class GoodsPortalShowServiceImpl extends ServiceImpl<GoodsPortalShowMapper, GoodsPortalShow> implements GoodsPortalShowService {
    @Override
    public GoodsPortalShow getOneByOption(String column, Object value) {
        QueryWrapper<GoodsPortalShow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return getOne(queryWrapper);
    }
}
