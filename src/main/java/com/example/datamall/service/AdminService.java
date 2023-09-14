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
    boolean isAdmin(Integer accountId);

    Admin getOneByOption(String column, Object value);
}
