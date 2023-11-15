package com.example.datamall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamall.entity.OrderGoods;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author woov
 * @since 2023-11-11
 */
public interface OrderGoodsService extends IService<OrderGoods> {

    OrderGoods getOneByOption(String column, Object value);
}
