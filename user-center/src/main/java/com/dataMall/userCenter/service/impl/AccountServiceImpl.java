package com.dataMall.userCenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.userCenter.entity.Account;
import com.dataMall.userCenter.mapper.AccountMapper;
import com.dataMall.userCenter.service.AccountService;
import com.dataMall.userCenter.utils.Sha256;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

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
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    //登录
    @Override
    public String login(String userName, String passWord) {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userName);
        queryWrapper.eq("password", passWord);
        Account account;
        try {
            account = getOne(queryWrapper);
            if (account == null) {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
        String token = Sha256.getSha256Str(userName + passWord + System.currentTimeMillis());
        account.setToken(token);
        update(account, queryWrapper);
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(String.valueOf(account.getId()), token, 60 * 60 * 24, TimeUnit.SECONDS);
        operations.set(token, account.toString(), 60 * 60 * 24, TimeUnit.SECONDS);
        return token;
    }

    //根据条件查询单个
    @Override
    public Account getOneByOption(String column, Object value) {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
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
    public void forget(String email, String password) {
        Account account = getOneByOption("email", email);
        account.setPassword(password);
        updateById(account);
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
    public boolean reg(String username, String password, String email) {
        Account accountInsert = new Account();
        accountInsert.setUsername(username);
        accountInsert.setPassword(password);
        accountInsert.setEmail(email);
        return save(accountInsert);
    }
}
