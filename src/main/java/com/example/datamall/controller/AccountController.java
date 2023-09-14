package com.example.datamall.controller;

import com.example.datamall.entity.Account;
import com.example.datamall.service.AccountService;
import com.example.datamall.service.AdminService;
import com.example.datamall.utils.EmailCode;
import com.example.datamall.utils.MailService;
import com.example.datamall.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 账号表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-09-14
 */
@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Resource
    private AccountService accountService;
    @Resource
    private AdminService adminService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private MailService mailService;
    @Resource
    private EmailCode emailCode;

    /**
     * description:管理员分页条件查询账号信息
     *
     * @param token：token
     * @param account：用户基础类
     * @param pageNum：页号
     * @param pageSize：页面大小
     * @return com.example.datamall.vo.DataView
     * @author woov
     * @create 2023/6/10
     **/
    @PostMapping("admin/query")
    public ResultData queryUserInfoPageByOption(@RequestHeader("token") String token, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody Account account) {
        boolean admin = accountService.checkAdminHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        String email = account.getEmail();
        String userName = account.getUsername();
        Integer id = account.getId();
        if (pageNum == null || pageSize == null) {
            return ResultData.fail("缺少参数");
        }
        return ResultData.success(accountService.queryUserPageByOption(id, userName, email, pageNum, pageSize));
    }

    /**
     * description:管理员通过用户基础账号id查找用户信息
     *
     * @param id：用户基础id
     * @param token：具有/admin路径权限的用户token
     * @return com.example.datamall.vo.DataView
     * @author woov
     * @create 2023/6/4
     **/
    @GetMapping("/admin/{id}")
    public ResultData findOne(@PathVariable Integer id, @RequestHeader("token") String token) {
        boolean admin = accountService.checkAdminHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        Account account = accountService.getById(id);
        return ResultData.success(account);
    }

    /**
     * description:用户登录
     *
     * @return com.example.datamall.vo.DataView
     * @author woov
     * @create 2023/6/4
     **/
    @PostMapping("/login")
    public ResultData login(@RequestBody Account account) {
        String username = account.getUsername();
        String password = account.getPassword();
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return ResultData.fail("缺少参数");
        }
        String token = accountService.login(username, password);
        if (token.isEmpty()) {
            return ResultData.fail("账号或密码错误");
        }
        Account account1 = accountService.getOneByOption("token", token);
        int uid = account1.getId();
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(String.valueOf(uid), token, 60 * 60 * 24, TimeUnit.SECONDS);
        operations.set(token, account1.toString(), 60 * 60 * 24, TimeUnit.SECONDS);
        Map<String, String> res = new HashMap<>();
        res.put("token", token);
        res.put("admin", String.valueOf(adminService.isAdmin(uid)));
        return ResultData.success(res);
    }

    /**
     * description:用户请求发送注册验证码
     *
     * @param email：用户接收验证码的邮箱
     * @return com.example.datamall.vo.DataView
     * @author woov
     * @create 2023/6/4
     **/
    @GetMapping("/reg/send_code/{email}")
    public ResultData sendRegAuthCode(@PathVariable String email) {
        if (email == null || email.isEmpty()) {
            return ResultData.fail("缺少参数");
        }
        Account account = accountService.getOneByOption("email", email);
        if (account != null) {
            return ResultData.fail("邮箱已注册");
        }
        mailService.sendCodeMessage(email, "注册");
        return ResultData.success();
    }

    /**
     * description:用户注册
     *
     * @param code：用户通过/user/reg/send_code/{email}请求的验证码
     * @param account：一个具有userName、passWord和email的一个用户基础Bean
     * @return com.example.datamall.vo.DataView
     * @author woov
     * @create 2023/6/4
     **/
    @PostMapping("/reg/{code}")
    public ResultData reg(@PathVariable String code, @RequestBody Account account) {
        String userName = account.getUsername();
        String passWord = account.getPassword();
        String email = account.getEmail();
        if (Objects.equals(userName, "") || userName == null || Objects.equals(passWord, "") || passWord == null || Objects.equals(email, "") || email == null || code == null) {
            return ResultData.fail("参数缺少");
        }
        if (emailCode.use(email, code)) {
            return ResultData.fail("验证码错误");
        }
        Account accountInsert = new Account();
        accountInsert.setUsername(userName);
        accountInsert.setPassword(passWord);
        accountInsert.setEmail(email);
        accountService.save(accountInsert);
        return ResultData.success();
    }

    //用户忘记密码-发送验证码
    @GetMapping("/forget/send_code/{email}")
    public ResultData sendForgetAUthCOde(@PathVariable String email) {
        if (email == null || email.isEmpty()) {
            return ResultData.fail("缺少参数");
        }
        Account account = accountService.getOneByOption("email", email);
        if (account == null) {
            return ResultData.fail("邮箱不存在");
        }
        mailService.sendCodeMessage(email, "修改密码");
        return ResultData.success();
    }

    //用户重置验证码
    @PostMapping("/forget/{code}")
    public ResultData forget(@PathVariable String code, @RequestBody Account account) {
        String password = account.getPassword();
        String email = account.getEmail();
        if (code == null || code.isEmpty() || password == null || password.isEmpty()) {
            return ResultData.fail("缺少参数");
        }
        if (emailCode.use(email, code)) {
            return ResultData.fail("验证码错误");
        }
        accountService.forget(email, password);
        return ResultData.success();
    }

    /**
     * description:校验用户token是否存在或过期
     *
     * @param token：用户token
     * @return com.example.datamall.vo.DataView
     * @author woov
     * @create 2023/6/4
     **/
    @GetMapping("/token")
    public ResultData checkToken(@RequestHeader("token") String token) {
        if (token == null || token.isEmpty()) {
            return ResultData.fail("参数缺少");
        }
        if (!accountService.checkTokenByRedis(token)) {
            return ResultData.fail("token失效");
        }
        Integer uid = accountService.tokenToUid(token);
        Account account = accountService.getById(uid);
        Map<String, String> res = new HashMap<>();
        res.put("admin", String.valueOf(adminService.isAdmin(uid)));
        res.put("userName", account.getUsername());
        return ResultData.success(res);
    }
}

