package com.example.datamall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamall.entity.GoodsCollection;

/**
 * <p>
 * 商品收藏表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
public interface GoodsCollectionService extends IService<GoodsCollection> {
    Long goodsCollectionNum(Integer goodsId);

    boolean follow(Integer accountId, Integer goodsId);

    boolean unfollow(Integer accountId, Integer goodsId);

    boolean isCollection(Integer uid, Integer goodsId);
}
