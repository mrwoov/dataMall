package com.dataMall.adminCenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.adminCenter.entity.GoodsPic;
import com.dataMall.adminCenter.mapper.GoodsPicMapper;
import com.dataMall.adminCenter.service.GoodsPicService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品图片表 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@Service
public class GoodsPicServiceImpl extends ServiceImpl<GoodsPicMapper, GoodsPic> implements GoodsPicService {

    @Override
    public List<String> getGoodsImageUrls(Integer goodsId) {
        List<String> imageUrls = new ArrayList<>();
        QueryWrapper<GoodsPic> picQueryWrapper = new QueryWrapper<>();
        picQueryWrapper.eq("goods_id", goodsId);
        List<GoodsPic> pics = list(picQueryWrapper);
        for (GoodsPic goodsPic : pics) {
            imageUrls.add(goodsPic.getUrl());
        }
        return imageUrls;
    }
}
