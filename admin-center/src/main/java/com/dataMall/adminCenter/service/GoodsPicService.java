package com.dataMall.adminCenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.adminCenter.entity.GoodsPic;

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
