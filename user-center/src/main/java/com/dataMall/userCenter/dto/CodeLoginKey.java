package com.dataMall.userCenter.dto;

import lombok.Data;

/**
 * @className: CodeLoginKey
 * @description: 用户扫码登录记录
 * @author: lxt
 * @create: 2021-06-06 11:51
 **/
@Data
public class CodeLoginKey {
    private Long id;
    private String eventKey;
    private String openId;
    private Integer uid;

    public CodeLoginKey(String eventKey, String openId,Integer uid) {
        this.eventKey = eventKey;
        this.openId = openId;
        this.uid = uid;
    }
}
