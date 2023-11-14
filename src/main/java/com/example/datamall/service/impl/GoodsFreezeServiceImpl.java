package com.example.datamall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamall.entity.GoodsFreeze;
import com.example.datamall.mapper.GoodsFreezeMapper;
import com.example.datamall.service.GoodsFreezeService;
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
        save(goodsFreeze);
        return false;
    }
}
