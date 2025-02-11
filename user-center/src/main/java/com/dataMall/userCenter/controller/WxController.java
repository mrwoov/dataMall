package com.dataMall.userCenter.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.User;
import com.dataMall.userCenter.config.WxConfig;
import com.dataMall.userCenter.dto.CodeLoginKey;
import com.dataMall.userCenter.service.SsoService;
import com.dataMall.userCenter.service.UserService;
import com.dataMall.userCenter.utils.CodeLoginUtil;
import com.dataMall.userCenter.utils.HttpClientUtils;
import com.dataMall.userCenter.utils.RedisUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/wx")
public class WxController {
    //测试模拟redis存储
    Map<String, CodeLoginKey> loginMap = new ConcurrentHashMap<>(64);
    @Autowired
    private WxConfig wxConfig;
    @Resource
    private UserService userService;
    @Resource
    private SsoService ssoService;
    @Resource
    private RedisUtils redisUtils;


    /**
     * 获取登录二维码
     *
     * @return
     */
    @GetMapping("/getQrCode")
    private BaseResponse getQrCode() {
        try {
            // 获取token开发者
            String accessToken = getAccessToken();
            if (StringUtils.isBlank(accessToken)) {
                return ResultUtils.error("获取accessToken失败");
            }
            String getQrCodeUrl = wxConfig.getQrCodeUrl();
            getQrCodeUrl = getQrCodeUrl.replace("TOKEN", accessToken);
            // 这里生成一个带参数的二维码，参数是scene_str
            String sceneStr = CodeLoginUtil.getRandomString(8);
            String json = "{\"expire_seconds\": 604800, \"action_name\": \"QR_STR_SCENE\"" + ", \"action_info\": {\"scene\": {\"scene_str\": \"" + sceneStr + "\"}}}";
            String result = HttpClientUtils.doPostJson(getQrCodeUrl, json);
            JSONObject jsonObject = JSONObject.parseObject(result);
            jsonObject.put("sceneStr", sceneStr);
            return ResultUtils.success(jsonObject);
        } catch (Exception e) {
            log.error("获取二维码失败", e);
            return ResultUtils.error(e.getMessage());
        }
    }

    /**
     * 获取accessToken
     *
     * @return
     */
    public String getAccessToken() throws Exception {
        String accessToken = redisUtils.get("wxAccessToken");
        if (StringUtils.isNotBlank(accessToken)) {
            return accessToken;
        }
        String getTokenUrl = wxConfig.getTokenUrl();
        getTokenUrl = getTokenUrl.replace("APPID", wxConfig.getAppId()).replace("SECRET", wxConfig.getSecret());
        String result = HttpClientUtils.doGet(getTokenUrl);
        JSONObject jsonObject = JSONObject.parseObject(result);
        accessToken = jsonObject.getString("access_token");
        redisUtils.set("wxAccessToken", accessToken, 60 * 60 * 2 - 60);
        return accessToken;
    }

    /**
     * 验证签名
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/checkSign")
    public String checkSign(HttpServletRequest request) throws Exception {
        log.info("===========>checkSign");
        //获取微信请求参数
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        //参数排序。 token 就要换成自己实际写的 token
        String[] params = new String[]{timestamp, nonce, "woov0815@"};
        Arrays.sort(params);
        //拼接
        String paramstr = params[0] + params[1] + params[2];
        //加密
        //获取 shal 算法封装类
        MessageDigest Sha1Dtgest = MessageDigest.getInstance("SHA-1");
        //进行加密
        byte[] digestResult = Sha1Dtgest.digest(paramstr.getBytes(StandardCharsets.UTF_8));
        //拿到加密结果
        String mysignature = CodeLoginUtil.bytes2HexString(digestResult);
        mysignature = mysignature.toLowerCase(Locale.ROOT);
        //是否正确
        boolean signsuccess = mysignature.equals(signature);
        //逻辑处理
        if (signsuccess && echostr != null) {
            return echostr;//不正确就直接返回失败提示．
        } else {
            WxMpXmlOutMessage jsonObject = callback(request);
            return jsonObject.toXml();
        }
    }


    /**
     * 回调方法
     *
     * @param request 请求
     * @return
     * @throws Exception
     */
    public WxMpXmlOutMessage callback(HttpServletRequest request) throws Exception {
        log.info("===========>callback");
        //request中有相应的信息，进行解析
        WxMpXmlMessage message = WxMpXmlMessage.fromXml(request.getInputStream());//获取消息流,并解析xml
        String messageType = message.getMsgType();                                //消息类型
        String messageEvent = message.getEvent();                                    //消息事件
        // openid
        String openId = message.getFromUser();//发送者帐号,就是openid
        String toUser = message.getToUser();                                    //发送者帐号
        String text = message.getContent();                                        //文本消息  文本内容
        // 生成二维码时穿过的特殊参数
        String eventKey = message.getEventKey();                                    //二维码参数
        Integer uid = ssoService.loginByOpenId(openId);
        boolean isBind = uid != null;
        User user = new User();
        if (isBind) {
            // 已经绑定过
            user = userService.getById(uid);
            if (user == null) {
                isBind = false;
            }
        }
        //if判断，判断查询
        String resContent = "";
        if (messageType.equals("event")) {
            //先根据openid从数据库查询  => 从自己数据库中查取用户信息 => jsonObject
            if (isBind) {
                resContent = "欢迎回来，" + user.getUsername();
            }
            //没有该用户
            if (StringUtils.isBlank(resContent)) {
                //从微信上中拉取用户信息
                resContent = "请在网页端绑定账号！";
            }
            // 扫码成功，存入缓存
            redisUtils.set("wxEventKey" + eventKey, openId, 60 * 5);
            return WxMpXmlOutMessage.TEXT().content(resContent)
                    .fromUser(toUser)
                    .toUser(openId)
                    .build();
        }
        return WxMpXmlOutMessage.TEXT().content(resContent)
                .fromUser(toUser)
                .toUser(openId)
                .build();
    }

    /**
     * 根据二维码标识获取用户openId=>获取用户信息
     *
     * @param eventKey 二维码标识
     * @return 用户openId
     */
    @GetMapping("/getOpenId/{eventKey}")
    public BaseResponse getOpenId(@PathVariable String eventKey) {
        String openId = redisUtils.get("wxEventKey" + eventKey);
        ResultUtils.throwIf(StringUtils.isBlank(openId), ErrorCode.FAIL, "未扫码成功！");
        // 扫码成功，删除缓存
        redisUtils.del(eventKey);
        return ResultUtils.success(openId);
    }
}
