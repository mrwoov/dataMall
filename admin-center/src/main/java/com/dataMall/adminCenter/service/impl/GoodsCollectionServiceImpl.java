package com.dataMall.adminCenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.adminCenter.entity.GoodsCollection;
import com.dataMall.adminCenter.mapper.GoodsCollectionMapper;
import com.dataMall.adminCenter.service.GoodsCollectionService;
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
        queryWrapper.eq("goods_id", goodsId);
        return count(queryWrapper);
    }

    @Override
    public boolean follow(Integer accountId, Integer goodsId) {
        GoodsCollection goodsCollection = new GoodsCollection();
        goodsCollection.setGoodsId(goodsId);
        goodsCollection.setUid(accountId);
        return save(goodsCollection);
    }

    @Override
    public boolean unfollow(Integer accountId, Integer goodsId) {
        QueryWrapper<GoodsCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", accountId);
        queryWrapper.eq("goods_id", goodsId);
        return remove(queryWrapper);
    }

    @Override
    public boolean isCollection(Integer uid, Integer goodsId) {
        QueryWrapper<GoodsCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("goods_id", goodsId);
        GoodsCollection goodsCollection = getOne(queryWrapper);
        return goodsCollection != null;
    }
}
