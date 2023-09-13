package com.example.datamall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamall.entity.UserBase;

/**
 * <p>
 * 基础用户表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-06-03
 */
public interface UserBaseService extends IService<UserBase> {

    String login(String userName, String passWord);

    UserBase getOneByOption(String column, Object value);

    boolean checkTokenByRedis(String token);

    boolean checkUserHavaAuth(String pathNow, String token);

    IPage<UserBase> queryUserPageByOption(Integer id, String userName, String email, Integer pageNum, Integer pageSize);

    void forget(String email, String password);

    Integer tokenToUid(String token);
}
