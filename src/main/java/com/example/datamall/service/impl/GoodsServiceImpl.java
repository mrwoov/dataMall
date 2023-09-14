package com.example.datamall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamall.entity.Goods;
import com.example.datamall.entity.UserBase;
import com.example.datamall.mapper.GoodsMapper;
import com.example.datamall.service.GoodsCategoriesService;
import com.example.datamall.service.GoodsCollectionService;
import com.example.datamall.service.GoodsService;
import com.example.datamall.service.UserBaseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {


    @Resource
    private GoodsCategoriesService goodsCategoriesService;
    @Resource
    private UserBaseService userBaseService;
    @Resource
    private GoodsCollectionService goodsCollectionService;

    @Override
    public boolean isOwner(Integer uid, Integer goodsId) {
        Integer uidTemp = getById(goodsId).getUid();
        if (uidTemp == null) {
            return false;
        }
        return uidTemp.equals(uid);
    }

    @Override
    public boolean userUpdateGoodsState(Integer uid, Integer goodsId, Integer state) {
        Goods goods = getById(goodsId);
        if (goods == null) {
            return false;
        }
        if (!goods.getUid().equals(uid)) {
            return false;
        }
        if (goods.getState() != 1) {
            return false;
        }
        goods.setState(1);
        return updateById(goods);
    }

    @Override
    public boolean adminUpdateGoodsState(Integer goodsId, Integer state) {
        Goods goods = getById(goodsId);
        if (goods == null) {
            return false;
        }
        goods.setState(state);
        return updateById(goods);
    }

    @Override
    public IPage<Goods> getGoodsPage(String name, String categoriesName, String username, Integer pageNum, Integer pageSize) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        if (categoriesName != null && !categoriesName.isEmpty()) {
            Integer categoriesId = goodsCategoriesService.getOneByOption("name", categoriesName).getId();
            queryWrapper.eq("categories_id", categoriesId);
        }
        if (username != null && !username.isEmpty()) {
            UserBase userBase = userBaseService.getOneByOption("user_name", username);
            if (userBase == null) {
                return new Page<>();
            }
            queryWrapper.eq("uid", userBase.getId());
        }
        IPage<Goods> page = page(new Page<>(pageNum, pageSize), queryWrapper);
        for (Goods goods : page.getRecords()) {
            goods.setUsername(userBaseService.getById(goods.getUid()).getUserName());
            goods.setCategoriesName(goodsCategoriesService.getById(goods.getCategoriesId()).getName());
            goods.priceToMoney();
        }
        return page;
    }

    @Override
    public List<Goods> getGoodsList(QueryWrapper<Goods> queryWrapper) {
        List<Goods> goodsList = list(queryWrapper);
        for (Goods goods : goodsList) {
            goods.setCategoriesName(goodsCategoriesService.getById(goods.getCategoriesId()).getName());
            goods.setUsername(userBaseService.getById(goods.getUid()).getUserName());
            goods.setCollection(goodsCollectionService.goodsCollectionNum(goods.getId()));
            goods.priceToMoney();
        }
        return goodsList;
    }
}
