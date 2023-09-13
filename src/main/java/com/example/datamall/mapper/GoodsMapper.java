package com.example.datamall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamall.entity.Goods;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

}
