package com.dataMall.adminCenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.adminCenter.mapper.UserMapper;
import com.dataMall.adminCenter.service.AdminService;
import com.dataMall.adminCenter.service.UserService;
import com.dataMall.adminCenter.utils.Sha256;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.entity.Admin;
import com.dataMall.common.entity.User;
import com.dataMall.common.exception.BusinessException;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private AdminService adminService;

    /**
     * 获取今日新增用户数
     *
     * @return 今日新增用户数
     */
    @Override
    public int getTodayNewUserCount() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("create_time", java.time.LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)); // 大于等于今天的开始时间
        queryWrapper.lt("create_time", java.time.LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999)); // 小于今天的结束时间
        return (int) count(queryWrapper);
    }

    /**
     * 获取昨日新增用户数
     *
     * @return 昨日新增用户数
     */
    @Override
    public int getYesterdayNewUserCount() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("create_time", java.time.LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0)); // 大于等于昨天的开始时间
        queryWrapper.lt("create_time", java.time.LocalDateTime.now().minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999)); // 小于昨天的结束时间
        return (int) count(queryWrapper);
    }

    /**
     * 获取本月新增用户数
     *
     * @return 本月新增用户数
     */
    @Override
    public int getThisMonthNewUserCount() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("create_time", java.time.LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)); // 大于等于本月的开始时间
        queryWrapper.lt("create_time", java.time.LocalDateTime.now());
        return (int) count(queryWrapper);
    }

    /**
     * 获取用户总数
     *
     * @return 用户总数
     */
    @Override
    public int getUserTotal() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        return (int) count(queryWrapper);
    }

    /**
     * 登录
     *
     * @param userName 用户名
     * @param passWord 密码
     * @return token
     * PS: 该方法废除，用ssoLogin代替
     */
    @Override
    public String login(String userName, String passWord) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userName);
        queryWrapper.eq("password", passWord);
        User user;
        try {
            user = getOne(queryWrapper);
            if (user == null) {
                throw new BusinessException(ErrorCode.FAIL, "用户名或密码错误");
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FAIL, "用户名或密码错误");
        }
        String token = Sha256.getSha256Str(userName + passWord + System.currentTimeMillis());
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

    //检查是否有管理员是否有权限
    @Override
    public boolean checkAdminHavaAuth(String pathNow, String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        try {
            User user = getOneByOption("token", token);
            Integer accountId = user.getId();
            Admin admin = adminService.getOneByOption("account_id", accountId);
            if (admin.getId() == null) {
                return false;
            }
            Integer roleId = admin.getRole();
            System.out.println(pathNow);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 管理员分页查询账号
    @Override
    public IPage<User> query(Integer id, String userName, String email, Integer pageNum, Integer pageSize) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
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

    //忘记账号密码
    @Override
    public void forget(String email, String password) {
        User user = getOneByOption("email", email);
        user.setPassword(password);
        updateById(user);
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
        User userInsert = new User();
        userInsert.setUsername(username);
        userInsert.setPassword(password);
        userInsert.setEmail(email);
        return save(userInsert);
    }
}
