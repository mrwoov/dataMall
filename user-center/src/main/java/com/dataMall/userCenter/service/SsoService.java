package com.dataMall.userCenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.userCenter.entity.Sso;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author woov
 * @since 2024-03-02
 */
public interface SsoService extends IService<Sso> {
    //用户名或邮箱登录
    int login(String userName, String passWord);

    //三方登录
    int login(String ssoType, String ssoUser, String ssoToken);

    //三方绑定账号
    boolean bind(int uid, String ssoType, String ssoUser, String ssoToken);
}
