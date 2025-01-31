package com.dataMall.userCenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.common.entity.User;


/**
 * <p>
 * 账号表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-09-14
 */
public interface UserService extends IService<User> {
    //忘记密码
    void forget(String email, String password);

    //注册
    void reg(String username, String password, String email);

    String login(int uid);

    //根据一个条件查找
    User getOneByOption(String column, Object value);

    //通过redis检查token
    boolean checkTokenByRedis(String token);

    //token转accountId
    Integer tokenToUid(String token);
}
