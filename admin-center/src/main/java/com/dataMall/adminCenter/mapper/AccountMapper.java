package com.dataMall.adminCenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataMall.adminCenter.entity.Account;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 账号表 Mapper 接口
 * </p>
 *
 * @author woov
 * @since 2023-09-14
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {

}
