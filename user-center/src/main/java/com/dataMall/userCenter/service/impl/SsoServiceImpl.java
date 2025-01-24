package com.dataMall.userCenter.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.Sso;
import com.dataMall.common.entity.SsoType;
import com.dataMall.userCenter.mapper.SsoMapper;
import com.dataMall.userCenter.service.SsoService;
import com.dataMall.userCenter.service.SsoTypeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author woov
 * @since 2024-03-02
 */
@Service
public class SsoServiceImpl extends ServiceImpl<SsoMapper, Sso> implements SsoService {

    @Resource
    private SsoTypeService ssoTypeService;
    

    //三方登录
    @Override
    public int login(String ssoTypeStr, String ssoUser, String ssoToken) {
        //抽象通用方法
        // 网页登录：用户名或邮箱登录
        if (ssoTypeStr.equals("web")) {
            return loginByUsernameOrEmail(ssoUser, ssoToken);
        }
        //其他类型登录
        QueryWrapper<SsoType> ssoTypeQueryWrapper = new QueryWrapper<>();
        ssoTypeQueryWrapper.eq("type", ssoTypeStr);
        SsoType ssoType = ssoTypeService.getOne(ssoTypeQueryWrapper);
        ResultUtils.throwIf(ssoType == null, ErrorCode.PARAMS_ERROR, "ssoType error");
        QueryWrapper<Sso> ssoQueryWrapper = new QueryWrapper<>();
        ssoQueryWrapper.eq("sso_user", ssoUser);
        ssoQueryWrapper.eq("sso_token", ssoToken);
        ssoQueryWrapper.eq("type", ssoType.getId());
        Sso sso = getOne(ssoQueryWrapper);
        ResultUtils.throwIf(sso == null, ErrorCode.FAIL, "username or password error");
        return sso.getUid();
    }

    //三方绑定账号(新增或换绑)
    @Override
    public boolean bind(int uid, String ssoTypeStr, String ssoUser, String ssoToken) {
        QueryWrapper<SsoType> ssoTypequeryWrapper = new QueryWrapper<>();
        ssoTypequeryWrapper.eq("type", ssoTypeStr);
        SsoType ssoType = ssoTypeService.getOne(ssoTypequeryWrapper);
        ResultUtils.throwIf(ssoType == null, ErrorCode.PARAMS_ERROR, "ssoType error");
        Sso sso;
        // 判断是否已经绑定过
        QueryWrapper<Sso> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sso_user", ssoUser);
        queryWrapper.eq("type", ssoType.getId());
        sso = getOne(queryWrapper);
        if (ObjectUtil.isNotNull(sso)) {
            // 已经绑定过
            sso.setSsoUser(ssoUser);
            sso.setSsoToken(ssoToken);
            return updateById(sso);
        }
        // 未绑定过
        sso.setUid(uid);
        sso.setSsoUser(ssoUser);
        sso.setSsoToken(ssoToken);
        sso.setType(ssoType.getId());
        return save(sso);
    }

    //用户名或邮箱登录
    public int loginByUsernameOrEmail(String userName, String passWord) {
        //邮箱或用户名登录
        QueryWrapper<Sso> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sso_user", userName);
        queryWrapper.eq("sso_token", passWord);
        queryWrapper.and(i -> i.eq("type", "2").or().eq("type", "3"));
        Sso sso = getOne(queryWrapper);
        ResultUtils.throwIf(sso == null, ErrorCode.FAIL, "username or password error");
        return sso.getUid();
    }
}
