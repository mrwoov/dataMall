package com.dataMall.adminCenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.adminCenter.entity.GoodsSnapshot;
import com.dataMall.adminCenter.entity.UserOrderGoods;
import com.dataMall.adminCenter.mapper.UserOrderGoodsMapper;
import com.dataMall.adminCenter.service.GoodsSnapshotService;
import com.dataMall.adminCenter.service.UserOrderGoodsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 订单商品表 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-11-16
 */
@Service
public class UserOrderGoodsServiceImpl extends ServiceImpl<UserOrderGoodsMapper, UserOrderGoods> implements UserOrderGoodsService {
    @Resource
    private GoodsSnapshotService goodsSnapshotService;

    @Override
    public boolean saveOrderGoods(Integer goodsId, Integer orderId) {
        UserOrderGoods userOrderGoods = new UserOrderGoods();
        Integer snapshotId = goodsSnapshotService.saveGoodsSnapshot(goodsId);
        userOrderGoods.setGoodsSnapshotId(snapshotId);
        userOrderGoods.setOrderId(orderId);
        return save(userOrderGoods);
    }

    @Override
    public List<GoodsSnapshot> getOrderGoodsSnapshot(Integer orderId) {
        QueryWrapper<UserOrderGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        List<GoodsSnapshot> snapshotList = new ArrayList<>();
        List<UserOrderGoods> userOrderGoodsList = list(queryWrapper);
        for (UserOrderGoods userOrderGoods : userOrderGoodsList) {
            GoodsSnapshot goodsSnapshot = goodsSnapshotService.getById(userOrderGoods.getGoodsSnapshotId());
            goodsSnapshot.setMoney((double) goodsSnapshot.getPrice() / 100);
            snapshotList.add(goodsSnapshot);
        }
        return snapshotList;
    }
}
