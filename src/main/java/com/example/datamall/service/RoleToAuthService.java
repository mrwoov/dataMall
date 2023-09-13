package com.example.datamall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamall.entity.RoleToAuth;

/**
 * <p>
 * 权限角色对应表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-06-04
 */
public interface RoleToAuthService extends IService<RoleToAuth> {

    RoleToAuth getOneByOption(String column, Object value);

    IPage<RoleToAuth> queryRTAInfoPageByOption(Integer pageSize, Integer pageNum, String roleName, String authName);
}
