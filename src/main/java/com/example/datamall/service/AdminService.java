package com.example.datamall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamall.entity.Admin;

/**
 * <p>
 * 管理员表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-09-14
 */
public interface AdminService extends IService<Admin> {
    //判断是否管理员
    boolean isAdmin(Integer accountId);

    //根据条件查询一个
    Admin getOneByOption(String column, Object value);
}
