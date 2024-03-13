package com.dataMall.userCenter.controller;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dataMall.userCenter.entity.Account;
import com.dataMall.userCenter.feign.AdminFeign;
import com.dataMall.userCenter.service.AccountService;
import com.dataMall.userCenter.service.SsoService;
import com.dataMall.userCenter.utils.EmailCode;
import com.dataMall.userCenter.utils.MailService;
import com.dataMall.userCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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
    private MailService mailService;
    @Resource
    private EmailCode emailCode;
    @Resource
    private AdminFeign adminFeign;
    @Resource
    private SsoService ssoService;

    //token和accountId是否为同一人
    @GetMapping("is_one/{accountId}")
    public ResultData isOne(@PathVariable Integer accountId, @RequestHeader("token") String token) {
        Integer aId = accountService.tokenToUid(token);
        return ResultData.state(aId.equals(accountId));
    }

    //获取用户信息
    @GetMapping("/userInfo/{uid}")
    public ResultData getUserInfo(@PathVariable int uid) {
        Account account = accountService.getOneByOption("id", uid);
        if (account == null) {
            return ResultData.fail();
        }
        account.declassify();
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
        int uid = ssoService.login(username, password);
        if (uid == -1) {
            return ResultData.fail("账号或密码错误");
        }
        String token = accountService.login(uid);
        if (token.isEmpty()) {
            return ResultData.fail("账号或密码错误");
        }
        Map<String, String> res = new HashMap<>();
        res.put("token", token);
        return ResultData.success(res);
    }

    @GetMapping(value = "/getWxCode")
    public ResultData getUserCode(@RequestParam("code") String code) {
        System.out.println(code);
        JSONObject jsonObject = new JSONObject();
        try {
            if (code == null || code.equals("")) {
                return ResultData.fail("缺少参数");
            }
            String url = "https://api.weixin.qq.com/sns/jscode2session" + "?appid=" + "wxa3242af9fc3e0245" + "&secret=" + "7ae0dfbf845f37db0d75b90628259b6b" + "&js_code=" + code + "&grant_type=authorization_code";
            String response = HttpUtil.get(url);
            jsonObject = JSON.parseObject(response);
            jsonObject.remove("session_key"); //删除session_key 避免泄露用户信息
        } catch (Exception ex) {
            jsonObject.put("errcode", "10004");
            jsonObject.put("errmsg", "获取失败，发生未知错误");
        }
        System.out.println(jsonObject);
        String openid = jsonObject.getString("openid");
        return ResultData.success(openid);
    }

    //三方登录
    @PostMapping("/sso_login")
    public ResultData ssoLogin(@RequestBody Account account) {
        //只有openid则密码为空
        String ssoType = account.getSsoType();
        String ssoUser = account.getUsername();
        String ssoToken = account.getPassword();
        if (ssoType == null || ssoType.isEmpty() || ssoUser == null || ssoUser.isEmpty() || ssoToken == null) {
            return ResultData.fail("缺少参数");
        }
        int uid = ssoService.login(ssoType, ssoUser, ssoToken);
        if (uid == -1) {
            return ResultData.fail("账号或密码错误");
        }
        String token = accountService.login(uid);
        if (token.isEmpty()) {
            return ResultData.fail("账号或密码错误");
        }
        Map<String, String> res = new HashMap<>();
        res.put("token", token);
        return ResultData.success(res);
    }

    //三方绑定账号
    @PostMapping("/bind")
    public ResultData bind(@RequestBody Account account) {
        String ssoType = account.getSsoType();
        String ssoUser = account.getUsername();
        String ssoToken = account.getPassword();
        String token = account.getToken();
        if (ssoType == null || ssoType.isEmpty() || ssoUser == null || ssoUser.isEmpty() || ssoToken == null || token == null || token.isEmpty()) {
            return ResultData.fail("缺少参数");
        }
        int uid = accountService.tokenToUid(token);
        if (uid == -1) {
            return ResultData.fail("token失效");
        }
        if (ssoService.bind(uid, ssoType, ssoUser, ssoToken)) {
            return ResultData.success();
        }
        return ResultData.fail();
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
            return ResultData.tokenFail();
        }
        Integer uid = accountService.tokenToUid(token);
        Account account = accountService.getById(uid);
        Map<String, String> res = new HashMap<>();
        res.put("admin", String.valueOf(adminFeign.isAdmin(uid)));
        res.put("username", account.getUsername());
        res.put("avatar", account.getAvatar());
        res.put("account_id", String.valueOf(account.getId()));
        return ResultData.success(res);
    }

    @GetMapping("/tokenToUid")
    public Integer tokenToUid(@RequestHeader("token") String token) {
        return accountService.tokenToUid(token);
    }

    @GetMapping("/getById/{id}")
    public Account getById(@PathVariable Integer id) {
        return accountService.getById(id);
    }

    @GetMapping("/getOneByOption")
    public Account getOneByOption(@RequestParam("column") String column, @RequestParam("value") String value) {
        return accountService.getOneByOption(column, value);
    }

    @GetMapping("getListByOption")
    public List<Account> usernameLikeList(@RequestParam("username") String username) {
        QueryWrapper<Account> accountQueryWrapper = new QueryWrapper<>();
        accountQueryWrapper.like("username", username);
        return accountService.list(accountQueryWrapper);
    }
}

