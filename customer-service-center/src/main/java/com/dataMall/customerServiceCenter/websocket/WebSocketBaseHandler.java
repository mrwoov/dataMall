package com.dataMall.customerServiceCenter.websocket;

import cn.hutool.json.JSONUtil;
import com.dataMall.customerServiceCenter.model.ServerMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 处理基类，封装公共方法
 */
public abstract class WebSocketBaseHandler extends TextWebSocketHandler {
    // 存储所有在线的 WebSocket 会话
    protected static final ConcurrentHashMap<String, WebSocketSession> onlineSessions = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        onlineSessions.put(session.getId(), session);
        sendMessage(session, new ServerMessage("connection_established", "连接已建立"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        onlineSessions.remove(session.getId());
        handleDisconnectedSession(session);
    }

    protected abstract void handleDisconnectedSession(WebSocketSession session);

    /**
     * 发送消息给指定的 WebSocket 会话
     */
    protected void sendMessage(WebSocketSession session, ServerMessage message) {
        try {
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(JSONUtil.toJsonStr(message)));
            }
        } catch (IOException e) {
            handleDisconnectedSession(session);
        }
    }

    /**
     * 发送错误消息
     */
    protected void sendError(WebSocketSession session, String errorMessage) {
        sendMessage(session, new ServerMessage("error", errorMessage));
    }
}
