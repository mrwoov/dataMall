package com.dataMall.adminCenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.adminCenter.entity.GoodsFreeze;
import com.dataMall.adminCenter.mapper.GoodsFreezeMapper;
import com.dataMall.adminCenter.service.GoodsFreezeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 冻结详解表 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-10-19
 */
@Service
public class GoodsFreezeServiceImpl extends ServiceImpl<GoodsFreezeMapper, GoodsFreeze> implements GoodsFreezeService {

    @Override
    public boolean option(Integer goodsId, Integer optionAccountId, boolean isFreeze, String content) {
        GoodsFreeze goodsFreeze = new GoodsFreeze();
        goodsFreeze.setGoodsId(goodsId);
        goodsFreeze.setAccountId(optionAccountId);
        goodsFreeze.setContent(content);
        Integer optionId = isFreeze ? -1 : 0;
        goodsFreeze.setOperate(optionId);
        return save(goodsFreeze);
    }
}
