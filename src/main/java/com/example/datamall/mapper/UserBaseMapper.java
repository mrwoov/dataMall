package com.example.datamall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamall.entity.UserBase;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 基础用户表 Mapper 接口
 * </p>
 *
 * @author woov
 * @since 2023-08-03
 */
@Mapper
public interface UserBaseMapper extends BaseMapper<UserBase> {

}
