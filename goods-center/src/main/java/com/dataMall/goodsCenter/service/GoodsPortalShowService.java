package com.dataMall.goodsCenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.goodsCenter.entity.GoodsPortalShow;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author woov
 * @since 2023-11-23
 */
public interface GoodsPortalShowService extends IService<GoodsPortalShow> {

    GoodsPortalShow getOneByOption(String column, Object value);
}
