package com.example.datamall.controller;

import com.example.datamall.entity.Account;
import com.example.datamall.service.AccountService;
import com.example.datamall.service.AdminService;
import com.example.datamall.utils.EmailCode;
import com.example.datamall.utils.MailService;
import com.example.datamall.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    private MailService mailService;
    @Resource
    private EmailCode emailCode;

    //管理员分页查账号信息
    @PostMapping("/admin/query")
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
        return ResultData.success(accountService.query(id, userName, email, pageNum, pageSize));
    }

    //管理员通过账号id查单个信息
    @GetMapping("/admin/{id}")
    public ResultData findOne(@PathVariable Integer id, @RequestHeader("token") String token) {
        boolean admin = accountService.checkAdminHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        Account account = accountService.getById(id);
        return ResultData.success(account);
    }

    //用户登录
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
        account = accountService.getOneByOption("token", token);
        Map<String, String> res = new HashMap<>();
        res.put("token", token);
        res.put("admin", String.valueOf(adminService.isAdmin(account.getId())));
        return ResultData.success(res);
    }

    //用户注册-验证码
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

    //用户注册
    @PostMapping("/reg/{code}")
    public ResultData reg(@PathVariable String code, @RequestBody Account account) {
        String username = account.getUsername();
        String password = account.getPassword();
        String email = account.getEmail();
        if (Objects.equals(username, "") || username == null || Objects.equals(password, "") || password == null || Objects.equals(email, "") || email == null || code == null) {
            return ResultData.fail("参数缺少");
        }
        if (emailCode.use(email, code)) {
            return ResultData.fail("验证码错误");
        }
        return ResultData.success(accountService.reg(username, password, email));
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

    //用户重置密码
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

    //校验用户token是否存在或过期
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
        res.put("username", account.getUsername());
        return ResultData.success(res);
    }
}

