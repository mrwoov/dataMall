package com.dataMall.customerServiceCenter.websocket;

import cn.hutool.json.JSONUtil;

import com.dataMall.customerServiceCenter.model.ServerMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
public class WSResultUtils {
    public static void sendMessage(WebSocketSession session, ServerMessage message) {
        try {
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(JSONUtil.toJsonStr(message)));
            }
        } catch (IOException e) {
            log.error("发送消息失败", e);
        }
    }

    public static void sendError(WebSocketSession session, String errorMessage) {
        sendMessage(session, new ServerMessage("error", errorMessage));
    }
    
}
