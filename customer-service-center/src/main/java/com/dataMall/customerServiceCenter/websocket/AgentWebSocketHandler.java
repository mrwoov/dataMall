package com.dataMall.customerServiceCenter.websocket;

import cn.hutool.json.JSONUtil;
import com.dataMall.customerServiceCenter.manager.AgentManager;
import com.dataMall.customerServiceCenter.model.ClientMessage;
import com.dataMall.customerServiceCenter.model.MessageType;
import com.dataMall.customerServiceCenter.model.ServerMessage;
import com.dataMall.customerServiceCenter.model.TransferRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.LinkedBlockingQueue;

public class AgentWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(AgentWebSocketHandler.class);
    private static final LinkedBlockingQueue<TransferRequest> transferQueue = new LinkedBlockingQueue<>();

    // 创建一个新的会话时
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 客服连接建立时，做一些初始化操作
        //todo: 客服登录
        AgentManager.agentLogin(session);  // 将该客服登录，保存到在线客服列表中
        WSResultUtils.sendMessage(session, new ServerMessage("agent_login_success", "客服登录成功"));
    }

    //关闭一个会话时
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        // 客服连接关闭时，执行注销操作
        AgentManager.agentLogout(session.getId());  // 客服注销
    }

    //处理传输错误时
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // 处理传输过程中出现的异常
        log.error("客服连接异常，Session ID: {}, 错误信息: {}", session.getId(), exception.getMessage());
        AgentManager.agentLogout(session.getId());  // 客服注销
    }

    //todo: 这里的实现逻辑有问题！(修改中)
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 处理接收到的消息
        ClientMessage clientMsg = JSONUtil.toBean(message.getPayload(), ClientMessage.class);
        if (clientMsg == null || clientMsg.getType() == null) {
            WSResultUtils.sendError(session, "消息格式错误");
            return;
        }
        if (clientMsg.getContent() == null) {
            WSResultUtils.sendError(session, "消息内容不能为空");
            return;
        }
        if (session.getAttributes().get("userId") == null && !clientMsg.getType().equals(MessageType.AGENT_LOGIN)) {
            // throw new WebSocketException(session, "客服未登录");
        }
        switch (clientMsg.getType()) {
            case MessageType.AGENT_LOGIN -> AgentManager.agentLogin(session); // 客服登录
            case MessageType.MESSAGE -> handleUserMessage(session, clientMsg); //客服发送消息
            case MessageType.AGENT_NEXT -> handleAgentNext(session, clientMsg); // 客服发起下一次服务请求（分配等待用户）
            case MessageType.AGENT_LOGOUT -> AgentManager.agentLogout(session.getId()); // 客服退出
            default -> WSResultUtils.sendError(session, "未知消息类型");
        }
        //System.out.println("收到用户消息: " + message.getPayload());
    }

    //处理客服回复消息
    private void handleUserMessage(WebSocketSession session, ClientMessage clientMsg) {
        String agentId = session.getId();
        String userId = AgentManager.getUserForAgent(agentId);
        if (userId == null) {
            WSResultUtils.sendError(session, "客服未绑定用户");
            return;
        }
        WebSocketSession userSession = AgentManager.getUserSession(userId);
        if (userSession == null) {
            WSResultUtils.sendError(session, "用户会话不存在");
            return;
        }
        WSResultUtils.sendMessage(userSession, new ServerMessage("agent_message", clientMsg.getContent()));
    }

    private void handleAgentNext(WebSocketSession session, ClientMessage clientMsg) {
        //取出等待队列中的下一个用户，并建立绑定关系
        String agentId = session.getId();
        String userId = AgentManager.assignNextUser(agentId);
        if (userId != null) {
            WebSocketSession userSession = AgentManager.getUserSession(userId);
            if (userSession != null && userSession.isOpen()) {
                WSResultUtils.sendMessage(userSession, new ServerMessage("transfer_success", "已分配客服"));
                WSResultUtils.sendMessage(session, new ServerMessage("agent_assigned", "已分配用户 " + userId));
            } else {
                WSResultUtils.sendMessage(session, new ServerMessage("agent_assigned", "分配的用户连接异常"));
            }
        } else {
            WSResultUtils.sendMessage(session, new ServerMessage("agent_waiting", "暂无等待用户"));
        }
    }

    // 处理客服退出登录，同时解绑与用户的绑定
    private void handleAgentLogout(WebSocketSession session, ClientMessage clientMsg) {
        String agentId = session.getId();
        // 若该客服已经绑定了用户，需要通知用户客服已下线
        String userId = AgentManager.getUserForAgent(agentId);
        if (userId != null) {
            WebSocketSession userSession = AgentManager.getUserSession(userId);
            if (userSession != null && userSession.isOpen()) {
                WSResultUtils.sendMessage(userSession, new ServerMessage("agent_logout", "客服已下线"));
            }
            AgentManager.unbindUserAgent(userId, agentId);
        }
        AgentManager.agentLogout(agentId);
        WSResultUtils.sendMessage(session, new ServerMessage("agent_logout_success", "客服已退出"));
    }
}
