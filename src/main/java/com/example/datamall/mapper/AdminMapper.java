package com.example.datamall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamall.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 管理员表 Mapper 接口
 * </p>
 *
 * @author woov
 * @since 2023-09-14
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

}
