package com.example.datamall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamall.entity.UserOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户订单表 Mapper 接口
 * </p>
 *
 * @author woov
 * @since 2023-11-16
 */

@Mapper

public interface UserOrderMapper extends BaseMapper<UserOrder> {

}
