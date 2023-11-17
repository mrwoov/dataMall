package com.dataMall.adminCenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.adminCenter.entity.Role;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-06-04
 */
public interface RoleService extends IService<Role> {

    Role getOneByOption(String column, Object value);

    IPage<Role> queryRoleInfoPageByOption(Integer pageSize, Integer pageNum, String roleName);
}
