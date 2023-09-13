package com.example.datamall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamall.entity.GoodsCategories;
import com.example.datamall.mapper.GoodsCategoriesMapper;
import com.example.datamall.service.GoodsCategoriesService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品分类表 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@Service
public class GoodsCategoriesServiceImpl extends ServiceImpl<GoodsCategoriesMapper, GoodsCategories> implements GoodsCategoriesService {

    @Override
    public IPage<GoodsCategories> queryGoodsCategoriesPageByOption(String name, String url, Integer pageNum, Integer pageSize) {
        QueryWrapper<GoodsCategories> queryWrapper = new QueryWrapper<>();
        if (name != null) {
            queryWrapper.like("name", name);
        }
        if (url != null) {
            queryWrapper.like("url", url);
        }
        return page(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @Override
    public GoodsCategories getOneByOption(String column, Object value) {
        QueryWrapper<GoodsCategories> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return getOne(queryWrapper);
    }
}
