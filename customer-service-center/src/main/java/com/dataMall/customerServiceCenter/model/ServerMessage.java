package com.dataMall.customerServiceCenter.model;

import java.util.Map;

/**
 * 服务器消息模型：
 * 1. 用于服务器向客户端发送消息；
 * 2. 包括类型和消息内容。
 */
public class ServerMessage {
    private String type; // 消息类型
    private Object content; // 消息内容

    public ServerMessage(String type, Object content) {
        this.type = type;
        this.content = content;
    }

    // 获取消息类型
    public String getType() {
        return type;
    }

    // 设置消息类型
    public void setType(String type) {
        this.type = type;
    }

    // 获取消息内容
    public Object getContent() {
        return content;
    }

    // 设置消息内容
    public void setContent(Object content) {
        this.content = content;
    }
}
