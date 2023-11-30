package com.dataMall.adminCenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.adminCenter.entity.GoodsPortalShow;
import com.dataMall.adminCenter.mapper.GoodsPortalShowMapper;
import com.dataMall.adminCenter.service.GoodsPortalShowService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-11-23
 */
@Service
public class GoodsPortalShowServiceImpl extends ServiceImpl<GoodsPortalShowMapper, GoodsPortalShow> implements GoodsPortalShowService {
    @Override
    public GoodsPortalShow getOneByOption(String column, Object value) {
        QueryWrapper<GoodsPortalShow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return getOne(queryWrapper);
    }

    @Override
    public boolean changeGoodsPortalShow(Integer goodsId, boolean state) {
        
        if (!state){
            QueryWrapper<GoodsPortalShow> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("goods_id",goodsId);
            System.out.println(1);
            return remove(queryWrapper);
        }
        GoodsPortalShow goodsPortalShow = new GoodsPortalShow();
        goodsPortalShow.setGoodsId(goodsId);
        System.out.println(2);
        return save(goodsPortalShow);
    }
}
