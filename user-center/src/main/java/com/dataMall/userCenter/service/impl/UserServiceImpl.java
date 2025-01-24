package com.dataMall.userCenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.Sso;
import com.dataMall.common.entity.User;
import com.dataMall.common.exception.BusinessException;
import com.dataMall.userCenter.mapper.UserMapper;
import com.dataMall.userCenter.service.SsoService;
import com.dataMall.userCenter.service.UserService;
import com.dataMall.userCenter.utils.Sha256;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 账号表 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-09-14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private SsoService ssoService;

    @Override
    public String login(int uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", uid);
        User user;
        try {
            user = getOne(queryWrapper);
            ResultUtils.throwIf(user == null, ErrorCode.FAIL, "user not found ");
            if (user.getState() != 0) {
                throw new BusinessException(ErrorCode.FAIL, "user is freeze");
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FAIL, "user not found");
        }
        String token = Sha256.getSha256Str(user.getUsername() + user.getPassword() + System.currentTimeMillis());
        user.setToken(token);
        update(user, queryWrapper);
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(String.valueOf(user.getId()), token, 60 * 60 * 24, TimeUnit.SECONDS);
        operations.set(token, user.toString(), 60 * 60 * 24, TimeUnit.SECONDS);
        return token;
    }

    //根据条件查询单个
    @Override
    public User getOneByOption(String column, Object value) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return getOne(queryWrapper);
    }

    //根据redis检查token
    @Override
    public boolean checkTokenByRedis(String token) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String userBaseStr = operations.get(token);
        if (userBaseStr == null || userBaseStr.isEmpty()) {
            return false;
        }

        int uid = findUidInStrByRegex(userBaseStr);
        if (uid == -1) {
            return false;
        }
        String redisToken = operations.get(String.valueOf(uid));
        return Objects.equals(redisToken, token);
    }


    //忘记账号密码
    @Override
    @Transactional
    public void forget(String email, String password) {
        User user = getOneByOption("email", email);
        ResultUtils.throwIf(user == null, ErrorCode.FAIL, "email not found");
        Integer uid = user.getId();
        //重置username密码
        QueryWrapper<Sso> ssoUsernameQueryWrapper = new QueryWrapper<>();
        ssoUsernameQueryWrapper.eq("uid", uid);
        ssoUsernameQueryWrapper.eq("type", 1);
        Sso ssoUsername = ssoService.getOne(ssoUsernameQueryWrapper);
        ssoUsername.setSsoToken(password);
        boolean ssoUsernameState = ssoService.update(ssoUsername, ssoUsernameQueryWrapper);
        ResultUtils.throwIfAndRollback(!ssoUsernameState, ErrorCode.FAIL, "update password fail in sso username");
        //重置email密码
        QueryWrapper<Sso> ssoEmailQueryWrapper = new QueryWrapper<>();
        ssoEmailQueryWrapper.eq("uid", uid);
        ssoEmailQueryWrapper.eq("type", 2);
        Sso ssoEmail = ssoService.getOne(ssoEmailQueryWrapper);
        ssoEmail.setSsoToken(password);
        boolean ssoEmailState = ssoService.update(ssoEmail, ssoEmailQueryWrapper);
        ResultUtils.throwIfAndRollback(!ssoEmailState, ErrorCode.FAIL, "update password fail in sso email");
    }

    //根据redis记录提取内容
    public int findUidInStrByRegex(String str) {
        String regex = "id=(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }

    //账号token转uid
    @Override
    public Integer tokenToUid(String token) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String res = operations.get(token);
        if (res == null) {
            return -1;
        }
        return findUidInStrByRegex(res);
    }

    //注册
    @Override
    @Transactional
    public boolean reg(String username, String password, String email) {
        // 先判断是否已经存在
        // 1.用户表
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username);
        userQueryWrapper.or();
        userQueryWrapper.eq("email", email);
        User userSQL = getOne(userQueryWrapper);
        ResultUtils.throwIf(userSQL != null, ErrorCode.FAIL, "user or email already exists");
        //2. sso表
        QueryWrapper<Sso> ssoQueryWrapper = new QueryWrapper<>();
        ssoQueryWrapper.eq("sso_user", username);
        ssoQueryWrapper.or();
        ssoQueryWrapper.eq("sso_user", email);
        Sso ssoSQL = ssoService.getOne(ssoQueryWrapper);
        ResultUtils.throwIf(ssoSQL != null, ErrorCode.FAIL, "user or email already exists");
        //保存用户表
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setState(0);
        boolean userState = save(user);
        ResultUtils.throwIfAndRollback(!userState, ErrorCode.FAIL, "user save fail in user");
        //保存sso表
        Sso ssoOfUsername = new Sso();
        ssoOfUsername.setSsoUser(username);
        ssoOfUsername.setSsoToken(password);
        ssoOfUsername.setType(1);
        boolean ssoOfUsernameState = ssoService.save(ssoOfUsername);
        ResultUtils.throwIfAndRollback(!ssoOfUsernameState, ErrorCode.FAIL, "user save fail in sso username");
        Sso ssoOfEmail = new Sso();
        ssoOfEmail.setSsoUser(email);
        ssoOfEmail.setSsoToken(password);
        ssoOfEmail.setType(2);
        boolean ssoOfEmailState = ssoService.save(ssoOfEmail);
        ResultUtils.throwIfAndRollback(!ssoOfEmailState, ErrorCode.FAIL, "user save fail in sso email");
        return true;
    }
}
