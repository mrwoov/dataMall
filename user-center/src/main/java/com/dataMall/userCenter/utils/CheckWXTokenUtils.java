package com.dataMall.userCenter.utils;

import lombok.extern.log4j.Log4j2;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 和微信建立链接参数校验
 */
@Log4j2
public class CheckWXTokenUtils {
    private static final String TOKEN = "woov0815@"; // 自定义的token

    /**
     * 校验微信服务器Token签名
     *
     * @param signature 微信加密签名
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return boolean
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) {
        String[] arr = {TOKEN, timestamp, nonce};
        Arrays.sort(arr);
        StringBuilder stringBuilder = new StringBuilder();
        for (String param : arr) {
            stringBuilder.append(param);
        }
        String hexString = SHA1(stringBuilder.toString());
        return signature.equals(hexString);
    }

    private static String SHA1(String str) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(str.getBytes());
            return toHexString(digest);
        } catch (NoSuchAlgorithmException e) {
            log.info("校验令牌Token出现错误：{}", e.getMessage());
        }
        return "";
    }

    /**
     * 字节数组转化为十六进制
     *
     * @param digest 字节数组
     * @return String
     */
    private static String toHexString(byte[] digest) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            String shaHex = Integer.toHexString(b & 0xff);
            if (shaHex.length() < 2) {
                hexString.append(0);
            }
            hexString.append(shaHex);
        }
        return hexString.toString();
    }
}
