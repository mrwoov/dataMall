package com.dataMall.userCenter.controller;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.User;
import com.dataMall.common.exception.BusinessException;
import com.dataMall.userCenter.feign.AdminFeign;
import com.dataMall.userCenter.service.SsoService;
import com.dataMall.userCenter.service.UserService;
import com.dataMall.userCenter.utils.EmailCode;
import com.dataMall.userCenter.utils.MailService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 账号表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-09-14
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Resource
    private UserService userService;
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
    public BaseResponse<Object> isOne(@PathVariable Integer accountId, @RequestHeader("token") String token) {
        Integer aId = userService.tokenToUid(token);
        boolean isOne = aId.equals(accountId);
        ResultUtils.throwIf(!isOne, ErrorCode.FAIL, "token和accountId不匹配");
        return ResultUtils.success();
    }

    //获取用户信息
    @GetMapping("/userInfo/{uid}")
    public BaseResponse<User> getUserInfo(@PathVariable int uid) {
        User user = userService.getOneByOption("id", uid);
        ResultUtils.throwIf(user == null, ErrorCode.FAIL, "用户不存在");
        user.declassify();
        return ResultUtils.success(user);
    }

    @GetMapping(value = "/getWxCode")
    public BaseResponse<String> getUserCode(@RequestParam("code") String code) {
        JSONObject jsonObject = new JSONObject();
        try {
            ResultUtils.throwIf(StringUtils.isBlank(code), ErrorCode.PARAMS_ERROR, "code为空");
            String url = "https://api.weixin.qq.com/sns/jscode2session" + "?appid=" + "wxa3242af9fc3e0245" + "&secret=" + "7ae0dfbf845f37db0d75b90628259b6b" + "&js_code=" + code + "&grant_type=authorization_code";
            String response = HttpUtil.get(url);
            jsonObject = JSON.parseObject(response);
            jsonObject.remove("session_key"); //删除session_key 避免泄露用户信息
        } catch (Exception ex) {
            jsonObject.put("errcode", "10004");
            jsonObject.put("errmsg", "获取失败，发生未知错误");
        }
        String openid = jsonObject.getString("openid");
        return ResultUtils.success(openid);
    }

    //登录
    @PostMapping("/login")
    public BaseResponse<Map<String, String>> login(@RequestBody User user) {
        //只有openid传入时，username和password同时传入openid
        String ssoType = user.getSsoType();
        String ssoUser = user.getUsername();
        String ssoToken = user.getPassword();
        if (StringUtils.isBlank(ssoType) || StringUtils.isBlank(ssoUser) || StringUtils.isBlank(ssoToken)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int uid = ssoService.login(ssoType, ssoUser, ssoToken);
        String token = userService.login(uid);
        ResultUtils.throwIf(StringUtils.isBlank(token), ErrorCode.FAIL, "账号或密码错误");
        Map<String, String> res = new HashMap<>();
        res.put("token", token);
        return ResultUtils.success(res);
    }

    //三方绑定账号
    @PostMapping("/bind")
    public BaseResponse<Object> bind(@RequestBody User user) {
        String ssoType = user.getSsoType();
        String ssoUser = user.getUsername();
        String ssoToken = user.getPassword();
        String token = user.getToken();
        if (StringUtils.isBlank(ssoType) || StringUtils.isBlank(ssoUser) || StringUtils.isBlank(ssoToken) || StringUtils.isBlank(token)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int uid = userService.tokenToUid(token);
        if (!ssoService.bind(uid, ssoType, ssoUser, ssoToken)) {
            throw new BusinessException(ErrorCode.FAIL, "绑定失败");
        }
        return ResultUtils.success();
    }

    //用户注册-验证码
    @GetMapping("/reg/send_code/{email}")
    public BaseResponse<Object> sendRegAuthCode(@PathVariable String email) {
        if (StringUtils.isBlank(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getOneByOption("email", email);
        ResultUtils.throwIf(user != null, ErrorCode.FAIL, "邮箱已注册");
        mailService.sendCodeMessage(email, "注册");
        return ResultUtils.success();
    }

    //用户注册
    @PostMapping("/reg/{code}")
    public BaseResponse<Boolean> reg(@PathVariable String code, @RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        String email = user.getEmail();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(email) || StringUtils.isBlank(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (emailCode.use(email, code)) {
            throw new BusinessException(ErrorCode.FAIL, "验证码错误");
        }
        userService.reg(username, password, email);
        return ResultUtils.success();
    }

    //用户忘记密码-发送验证码
    @GetMapping("/forget/send_code/{email}")
    public BaseResponse<Object> sendForgetAUthCOde(@PathVariable String email) {
        if (email == null || email.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getOneByOption("email", email);
        if (user == null) {
            throw new BusinessException(ErrorCode.FAIL, "邮箱不存在");
        }
        mailService.sendCodeMessage(email, "修改密码");
        return ResultUtils.success();
    }

    //用户重置密码
    @PostMapping("/forget/{code}")
    public BaseResponse<Object> forget(@PathVariable String code, @RequestBody User user) {
        String password = user.getPassword();
        String email = user.getEmail();
        if (StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (emailCode.use(email, code)) {
            throw new BusinessException(ErrorCode.FAIL, "验证码错误");
        }
        userService.forget(email, password);
        return ResultUtils.success();
    }


    //校验用户token是否存在或过期
    @GetMapping("/token")
    public BaseResponse<Map<String, String>> checkToken(@RequestHeader("token") String token) {
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (!userService.checkTokenByRedis(token)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Integer uid = userService.tokenToUid(token);
        User user = userService.getById(uid);
        Map<String, String> res = new HashMap<>();
        res.put("admin", String.valueOf(adminFeign.isAdmin(uid)));
        res.put("username", user.getUsername());
        res.put("avatar", user.getAvatar());
        res.put("account_id", String.valueOf(user.getId()));
        return ResultUtils.success(res);
    }

    @GetMapping("/tokenToUid")
    public Integer tokenToUid(@RequestHeader("token") String token) {
        return userService.tokenToUid(token);
    }

    @GetMapping("/getById/{id}")
    public User getById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @GetMapping("/getOneByOption")
    public User getOneByOption(@RequestParam("column") String column, @RequestParam("value") String value) {
        return userService.getOneByOption(column, value);
    }

    @GetMapping("getListByOption")
    public List<User> usernameLikeList(@RequestParam("username") String username) {
        QueryWrapper<User> accountQueryWrapper = new QueryWrapper<>();
        accountQueryWrapper.like("username", username);
        return userService.list(accountQueryWrapper);
    }
}

