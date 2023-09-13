package com.example.datamall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamall.entity.Goods;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
public interface GoodsService extends IService<Goods> {
    boolean isOwner(Integer uid, Integer goodsId);

    boolean userUpdateGoodsState(Integer uid, Integer goodsId, Integer state);

    boolean adminUpdateGoodsState(Integer goodsId, Integer state);

    IPage<Goods> getGoodsPage(String name, String categoriesName, String username, Integer pageNum, Integer pageSize);
}
