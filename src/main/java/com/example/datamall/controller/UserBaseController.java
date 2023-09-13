package com.example.datamall.controller;

import com.example.datamall.entity.UserBase;
import com.example.datamall.service.UserBaseService;
import com.example.datamall.utils.EmailCode;
import com.example.datamall.utils.MailService;
import com.example.datamall.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 基础用户表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-06-03
 */
@RestController
@RequestMapping("/users")
public class UserBaseController {
    @Resource
    private UserBaseService userBaseService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private MailService mailService;
    @Resource
    private EmailCode emailCode;

    /**
     * description:管理员新增或修改用户
     *
     * @param userBase：传入需要修改的用户基础Bean
     * @param token：具有/admin路径权限的用户token
     * @return com.example.datamall.vo.DataView
     * @author woov
     * @create 2023/6/4
     **/
    @PatchMapping("/admin")
    public ResultData save(@RequestBody UserBase userBase, @RequestHeader("token") String token) {
        boolean admin = userBaseService.checkUserHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        boolean state = userBaseService.saveOrUpdate(userBase);
        return ResultData.state(state);
    }

    /**
     * description:管理员删除用户
     *
     * @param id：需要删除的用户id
     * @param token：具有/admin路径权限的用户token
     * @return com.example.datamall.vo.DataView
     * @author woov
     * @create 2023/6/4
     **/
    @DeleteMapping("/admin/{id}")
    public ResultData delete(@PathVariable Integer id, @RequestHeader("token") String token) {
        boolean admin = userBaseService.checkUserHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        boolean state = userBaseService.removeById(id);
        return ResultData.state(state);
    }

    /**
     * description:管理员批量删除用户
     *
     * @param ids：由需要删除的用户的id组成的list
     * @param token：具有/admin路径权限的用户token
     * @return com.example.datamall.vo.DataView
     * @author woov
     * @create 2023/6/4
     **/
    @PostMapping("/admin/del_batch")
    public ResultData deleteBatch(@RequestBody List<Integer> ids, @RequestHeader("token") String token) {
        boolean admin = userBaseService.checkUserHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        boolean state = userBaseService.removeByIds(ids);
        return ResultData.state(state);
    }

    /**
     * description:管理员分页条件查询用户信息
     *
     * @param token：token
     * @param userBase：用户基础类
     * @param pageNum：页号
     * @param pageSize：页面大小
     * @return com.example.datamall.vo.DataView
     * @author woov
     * @create 2023/6/10
     **/
    @PostMapping("admin/query")
    public ResultData queryUserInfoPageByOption(@RequestHeader("token") String token, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody UserBase userBase) {
        boolean admin = userBaseService.checkUserHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        String email = userBase.getEmail();
        String userName = userBase.getUserName();
        Integer id = userBase.getId();
        if (pageNum == null || pageSize == null) {
            return ResultData.fail("缺少参数");
        }
        return ResultData.success(userBaseService.queryUserPageByOption(id, userName, email, pageNum, pageSize));
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
        boolean admin = userBaseService.checkUserHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        UserBase userBase = userBaseService.getById(id);
        return ResultData.success(userBase);
    }

    /**
     * description:用户登录
     *
     * @return com.example.datamall.vo.DataView
     * @author woov
     * @create 2023/6/4
     **/
    @PostMapping("/login")
    public ResultData login(@RequestBody UserBase userBase) {
        String username = userBase.getUserName();
        String password = userBase.getPassWord();
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return ResultData.fail("缺少参数");
        }
        String token = userBaseService.login(username, password);
        if (token.isEmpty()) {
            return ResultData.fail("账号或密码错误");
        }
        UserBase userBase1 = userBaseService.getOneByOption("token", token);
        int uid = userBase1.getId();
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(String.valueOf(uid), token, 60 * 60 * 24, TimeUnit.SECONDS);
        operations.set(token, userBase1.toString(), 60 * 60 * 24, TimeUnit.SECONDS);
        Map<String, String> res = new HashMap<>();
        res.put("token", token);
        res.put("role", String.valueOf(userBase1.getRole()));
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
        UserBase userBase = userBaseService.getOneByOption("email", email);
        if (userBase != null) {
            return ResultData.fail("邮箱已注册");
        }
        mailService.sendCodeMessage(email, "注册");
        return ResultData.success();
    }

    /**
     * description:用户注册
     *
     * @param code：用户通过/user/reg/send_code/{email}请求的验证码
     * @param userBase：一个具有userName、passWord和email的一个用户基础Bean
     * @return com.example.datamall.vo.DataView
     * @author woov
     * @create 2023/6/4
     **/
    @PostMapping("/reg/{code}")
    public ResultData reg(@PathVariable String code, @RequestBody UserBase userBase) {
        String userName = userBase.getUserName();
        String passWord = userBase.getPassWord();
        String email = userBase.getEmail();
        if (Objects.equals(userName, "") || userName == null || Objects.equals(passWord, "") || passWord == null || Objects.equals(email, "") || email == null || code == null) {
            return ResultData.fail("参数缺少");
        }
        if (emailCode.use(email, code)) {
            return ResultData.fail("验证码错误");
        }
        UserBase userBaseInsert = new UserBase();
        userBaseInsert.setUserName(userName);
        userBaseInsert.setPassWord(passWord);
        userBaseInsert.setEmail(email);
        userBaseService.save(userBaseInsert);
        return ResultData.success();
    }

    @GetMapping("/forget/send_code/{email}")
    public ResultData sendForgetAUthCOde(@PathVariable String email) {
        if (email == null || email.isEmpty()) {
            return ResultData.fail("缺少参数");
        }
        UserBase userBase = userBaseService.getOneByOption("email", email);
        if (userBase == null) {
            return ResultData.fail("邮箱不存在");
        }
        mailService.sendCodeMessage(email, "修改密码");
        return ResultData.success();
    }

    @PostMapping("/forget/{code}")
    public ResultData forget(@PathVariable String code, @RequestBody UserBase userBase) {
        String password = userBase.getPassWord();
        String email = userBase.getEmail();
        if (code == null || code.isEmpty() || password == null || password.isEmpty()) {
            return ResultData.fail("缺少参数");
        }
        if (emailCode.use(email, code)) {
            return ResultData.fail("验证码错误");
        }
        userBaseService.forget(email, password);
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
        if (!userBaseService.checkTokenByRedis(token)) {
            return ResultData.fail("token失效");
        }
        Integer uid = userBaseService.tokenToUid(token);
        UserBase userBase = userBaseService.getById(uid);
        Map<String,String> res = new HashMap<>();
        res.put("role", String.valueOf(userBase.getRole()));
        res.put("userName",userBase.getUserName());
        return ResultData.success(res);
    }
}