package com.dataMall.adminCenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.adminCenter.entity.Goods;
import com.dataMall.adminCenter.entity.GoodsSnapshot;
import com.dataMall.adminCenter.mapper.GoodsSnapshotMapper;
import com.dataMall.adminCenter.service.GoodsService;
import com.dataMall.adminCenter.service.GoodsSnapshotService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品快照表 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-11-16
 */
@Service
public class GoodsSnapshotServiceImpl extends ServiceImpl<GoodsSnapshotMapper, GoodsSnapshot> implements GoodsSnapshotService {
    @Resource
    private GoodsService goodsService;

    @Override
    public Integer saveGoodsSnapshot(Integer goodsId) {
        Goods goods = goodsService.getById(goodsId);
        Integer snapshotId = isExistSnapshot(goods);
        if (snapshotId != -1) {
            return snapshotId;
        }
        GoodsSnapshot goodsSnapshot = new GoodsSnapshot();
        goodsSnapshot.setOwnerId(goods.getUid());
        goodsSnapshot.setGoodsId(goods.getId());
        goodsSnapshot.setName(goods.getName());
        goodsSnapshot.setPicIndex(goods.getPicIndex());
        goodsSnapshot.setPrice(goods.getPrice());
        goodsSnapshot.setFileMd5(goods.getFileMd5());
        save(goodsSnapshot);
        return goodsSnapshot.getId();
    }

    @Override
    public Integer isExistSnapshot(Goods goods) {
        QueryWrapper<GoodsSnapshot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id", goods.getUid());
        queryWrapper.eq("goods_id", goods.getId());
        queryWrapper.eq("name", goods.getName());
        queryWrapper.eq("pic_index", goods.getPicIndex());
        queryWrapper.eq("price", goods.getPrice());
        queryWrapper.eq("file_md5", goods.getFileMd5());
        GoodsSnapshot goodsSnapshot = getOne(queryWrapper);
        if (goodsSnapshot == null) {
            return -1;
        }
        return goodsSnapshot.getId();
    }
}
