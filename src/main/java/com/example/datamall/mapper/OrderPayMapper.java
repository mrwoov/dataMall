package com.example.datamall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamall.entity.OrderPay;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author woov
 * @since 2023-11-11
 */

@Mapper

public interface OrderPayMapper extends BaseMapper<OrderPay> {

}
