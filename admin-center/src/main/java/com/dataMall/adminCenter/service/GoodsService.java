package com.dataMall.adminCenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.adminCenter.entity.Goods;

import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
public interface GoodsService extends IService<Goods> {
    //获取商品总数
    Integer getGoodsCount();

    //获取未审核的商品的数量
    Integer getNotAuditGoodsCount();

    //获取上架商品数量
    Integer getNormalGoodsCount();

    boolean isOwner(Integer uid, Integer goodsId);

    void getGoodsOtherParam(Goods goods);

    void getGoodsListOtherParam(List<Goods> goodsList);

    Goods getGoodsInfoById(Integer id);

    Goods getNotAuditGoods();

    boolean userUpdateGoodsState(Integer uid, Integer goodsId, Integer state);

    boolean adminUpdateGoodsState(Integer goodsId, Integer state);

    IPage<Goods> getGoodsPage(String name, String categoriesName, String username, Integer pageNum, Integer pageSize);

    List<Goods> getGoodsList(QueryWrapper<Goods> queryWrapper);
}
