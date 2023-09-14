package com.example.datamall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamall.entity.Account;
import com.example.datamall.entity.Auth;
import com.example.datamall.entity.Role;
import com.example.datamall.entity.RoleToAuth;
import com.example.datamall.mapper.AccountMapper;
import com.example.datamall.service.AccountService;
import com.example.datamall.service.AuthService;
import com.example.datamall.service.RoleService;
import com.example.datamall.service.RoleToAuthService;
import com.example.datamall.utils.Sha256;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
    private AuthService authService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private RoleService roleService;
    @Resource
    private RoleToAuthService roleToAuthService;

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
        return token;
    }

    @Override
    public Account getOneByOption(String column, Object value) {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return getOne(queryWrapper);
    }

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

    @Override
    public boolean checkUserHavaAuth(String pathNow, String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        try {
//            Account account = getOneByOption("token", token);
//            int roleId = account.getRole();
//            QueryWrapper<RoleToAuth> roleToAuthQueryWrapper = new QueryWrapper<>();
//            roleToAuthQueryWrapper.eq("role_id", roleId);
//            List<RoleToAuth> roleToAuthList = roleToAuthService.list(roleToAuthQueryWrapper);
//            for (RoleToAuth tempRoleToAuth : roleToAuthList) {
//                int authId = tempRoleToAuth.getAuthId();
//                Auth auth = authService.getById(authId);
//                String authPath = auth.getPath();
//                if (Objects.equals(authPath, "/") || Objects.equals(authPath, pathNow)) {
//                    return true;
//                }
//            }
            //todo: 重写逻辑
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public IPage<Account> queryUserPageByOption(Integer id, String userName, String email, Integer pageNum, Integer pageSize) {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        if (id != null) {
            queryWrapper.eq("id", id);
        }
        if (userName != null && !userName.isEmpty()) {
            queryWrapper.like("userName", userName);
        }
        if (email != null && !email.isEmpty()) {
            queryWrapper.like("email", email);
        }
        return page(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @Override
    public void forget(String email, String password) {
        Account account = getOneByOption("email", email);
        account.setPassword(password);
        updateById(account);
    }


    public int findUidInStrByRegex(String str) {
        String regex = "id=(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }

    @Override
    public Integer tokenToUid(String token) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String res = operations.get(token);
        if (res == null) {
            return -1;
        }
        return findUidInStrByRegex(res);
    }
}
