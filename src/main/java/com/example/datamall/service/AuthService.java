package com.example.datamall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamall.entity.Auth;

import java.util.List;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-06-04
 */
public interface AuthService extends IService<Auth> {

    Auth getOneByOption(String column, Object value);

    IPage<Auth> queryAuthInfoPageByOption(Integer pageNum, Integer pageSize, Integer id, String path, String name);

    Boolean delChildId(Integer pid);

    List<Auth> getAuthTree();
}
