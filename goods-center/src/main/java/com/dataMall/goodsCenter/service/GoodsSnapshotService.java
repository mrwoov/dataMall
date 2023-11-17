package com.dataMall.goodsCenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.goodsCenter.entity.Goods;
import com.dataMall.goodsCenter.entity.GoodsSnapshot;

/**
 * <p>
 * 商品快照表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-11-16
 */
public interface GoodsSnapshotService extends IService<GoodsSnapshot> {

    Integer saveGoodsSnapshot(Integer goodsId);

    Integer isExistSnapshot(Goods goods);
}
