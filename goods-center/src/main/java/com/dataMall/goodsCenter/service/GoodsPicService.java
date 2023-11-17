package com.dataMall.goodsCenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.goodsCenter.entity.GoodsPic;

import java.util.List;

/**
 * <p>
 * 商品图片表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
public interface GoodsPicService extends IService<GoodsPic> {
    List<String> getGoodsImageUrls(Integer goodsId);
}
