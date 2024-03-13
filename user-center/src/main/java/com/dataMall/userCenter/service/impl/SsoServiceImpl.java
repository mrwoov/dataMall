package com.dataMall.userCenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.userCenter.entity.Sso;
import com.dataMall.userCenter.entity.SsoType;
import com.dataMall.userCenter.mapper.SsoMapper;
import com.dataMall.userCenter.service.SsoService;
import com.dataMall.userCenter.service.SsoTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author woov
 * @since 2024-03-02
 */
@Service
public class SsoServiceImpl extends ServiceImpl<SsoMapper, Sso> implements SsoService {

    @Autowired
    private SsoTypeService ssoTypeService;
    @Override
    public int login(String userName, String passWord) {
        //邮箱或用户名登录
        QueryWrapper<Sso> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sso_user", userName);
        queryWrapper.eq("sso_token", passWord);
        queryWrapper.and(i -> i.eq("type", "2").or().eq("type", "3"));
        Sso sso = getOne(queryWrapper);
        if (sso == null) {
            return -1;
        }
        return sso.getUid();
    }

    //三方登录
    @Override
    public int login(String ssoType, String ssoUser, String ssoToken) {
        QueryWrapper<SsoType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", ssoType);
        SsoType ssoType1 = ssoTypeService.getOne(queryWrapper);
        if (ssoType1 == null) {
            return -1;
        }
        QueryWrapper<Sso> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("sso_user", ssoUser);
        queryWrapper1.eq("sso_token", ssoToken);
        queryWrapper1.eq("type", ssoType1.getId());
        Sso sso = getOne(queryWrapper1);
        if (sso == null) {
            return -1;
        }
        return sso.getUid();
    }

    //三方绑定账号
    @Override
    public boolean bind(int uid, String ssoType, String ssoUser, String ssoToken) {
        QueryWrapper<SsoType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", ssoType);
        SsoType ssoType1 = ssoTypeService.getOne(queryWrapper);
        if (ssoType1 == null) {
            System.out.println(1);
            return false;
        }
        Sso sso =new Sso();
        sso.setUid(uid);
        sso.setSsoUser(ssoUser);
        sso.setSsoToken(ssoToken);
        sso.setType(ssoType1.getId());
        //如果已经绑定过了
        QueryWrapper<Sso> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("sso_user", ssoUser);
        queryWrapper1.eq("type", ssoType1.getId());
        Sso sso1 = getOne(queryWrapper1);
        if (sso1 != null) {
            return false;
        }
        return save(sso);
    }
}
