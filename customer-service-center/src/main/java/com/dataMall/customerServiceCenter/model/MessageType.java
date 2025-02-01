package com.dataMall.customerServiceCenter.model;

/**
 * 消息类型常量接口，定义用户与客服相关的操作类型。
 */
public interface MessageType {
    // 用户相关
    String INIT = "init";             // 用户初始化连接（验证身份）
    String MESSAGE = "message";       // 用户发送消息
    String TRANSFER = "transfer";     // 用户发起转人工请求
    String AGENT_STATUS = "status";   // 用户查询客服状态
    // 用户相关
    String CANCEL_TRANSFER = "cancel_transfer"; // 用户取消排队

    // 客服相关
    String AGENT_LOGIN = "agent_login";   // 客服登录
    String AGENT_LOGOUT = "agent_logout";   // 客服退出
    String AGENT_NEXT = "agent_next";       // 客服发起下一次服务请求（分配等待用户）
}
