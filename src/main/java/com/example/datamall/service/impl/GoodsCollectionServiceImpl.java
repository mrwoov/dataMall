package com.example.datamall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamall.entity.GoodsCollection;
import com.example.datamall.mapper.GoodsCollectionMapper;
import com.example.datamall.service.GoodsCollectionService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品收藏表 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@Service
public class GoodsCollectionServiceImpl extends ServiceImpl<GoodsCollectionMapper, GoodsCollection> implements GoodsCollectionService {

    @Override
    public Long goodsCollectionNum(Integer goodsId) {
        QueryWrapper<GoodsCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goods_id",goodsId);
        return count(queryWrapper);
    }
}
