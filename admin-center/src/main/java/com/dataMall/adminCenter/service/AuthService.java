package com.dataMall.adminCenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.adminCenter.entity.Auth;

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
    //根据条件查询单个
    Auth getOneByOption(String column, Object value);

    Boolean del(Integer id);

    Boolean delChildId(Integer pid);

    List<Auth> getAuthTree();

    List<Auth> listToTree(List<Auth> list);

    List<Auth> getChild(Integer parentId);
}
