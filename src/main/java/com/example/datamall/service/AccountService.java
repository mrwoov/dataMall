package com.example.datamall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamall.entity.Account;

/**
 * <p>
 * 账号表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-09-14
 */
public interface AccountService extends IService<Account> {
    //登录
    String login(String userName, String passWord);

    //忘记密码
    void forget(String email, String password);

    //注册
    boolean reg(String username, String password, String email);

    //根据一个条件查找
    Account getOneByOption(String column, Object value);

    //通过redis检查token
    boolean checkTokenByRedis(String token);

    //检查管理员是否有权限
    boolean checkAdminHavaAuth(String pathNow, String token);

    //管理员分页查找
    IPage<Account> query(Integer id, String userName, String email, Integer pageNum, Integer pageSize);

    //token转accountId
    Integer tokenToUid(String token);
}
