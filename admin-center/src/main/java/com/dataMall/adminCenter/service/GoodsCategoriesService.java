package com.dataMall.adminCenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.adminCenter.entity.GoodsCategories;

/**
 * <p>
 * 商品分类表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
public interface GoodsCategoriesService extends IService<GoodsCategories> {

    IPage<GoodsCategories> queryGoodsCategoriesPageByOption(String name, String url, Integer pageNum, Integer pageSize);

    GoodsCategories getOneByOption(String column, Object value);
}
