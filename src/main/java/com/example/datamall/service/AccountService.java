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
    String login(String userName, String passWord);

    Account getOneByOption(String column, Object value);

    boolean checkTokenByRedis(String token);


    boolean checkAdminHavaAuth(String pathNow, String token);

    IPage<Account> queryUserPageByOption(Integer id, String userName, String email, Integer pageNum, Integer pageSize);

    void forget(String email, String password);

    Integer tokenToUid(String token);
}
