package com.dataMall.goodsCenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.goodsCenter.entity.GoodsFreeze;

/**
 * <p>
 * 冻结详解表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-10-19
 */
public interface GoodsFreezeService extends IService<GoodsFreeze> {
    boolean option(Integer goodsId, Integer optionAccountId, boolean isFreeze, String content);
}
