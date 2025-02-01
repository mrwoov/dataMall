package com.dataMall.customerServiceCenter.model;

import lombok.Data;

/**
 * 客户端发送的消息格式，包含消息类型、token（用于身份验证）以及消息内容。
 */
@Data
public class ClientMessage {
    private String type;
    private String token;
    private String content;
}
