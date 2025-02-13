package com.dataMall.customerServiceCenter.websocket;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dataMall.customerServiceCenter.config.Knowledge;
import com.dataMall.customerServiceCenter.dto.AIResponse;
import com.dataMall.customerServiceCenter.feign.UserService;
import com.dataMall.customerServiceCenter.manager.AIManager;
import com.dataMall.customerServiceCenter.manager.AgentManager;
import com.dataMall.customerServiceCenter.model.ClientMessage;
import com.dataMall.customerServiceCenter.model.MessageType;
import com.dataMall.customerServiceCenter.model.ServerMessage;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 用户 WebSocket 处理器：
 * 1. 用户连接后默认使用 AI 自动回复；
 * 2. 可发送请求转接人工客服，并进入排队队列；
 * 3. 处理用户消息转发，若分配客服则转发给对应客服，否则使用 AI 回复；
 */
public class CustomerWebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(CustomerWebSocketHandler.class);
    //    // 所有在线用户会话（包括用户和客服）
//    private static final ConcurrentHashMap<String, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();
//    // 排队等待人工客服的用户队列
    //private static final LinkedBlockingQueue<TransferRequest> transferQueue = new LinkedBlockingQueue<>();
    @Resource
    private AIManager aiManager;
    @Resource
    private UserService userService;

    // 用户连接建立时
    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        AgentManager.addUserSession(session.getId(), session);
        WSResultUtils.sendMessage(session, new ServerMessage("connection_established", "您好，欢迎使用智能客服服务，请描述您的问题"));
    }

    //关闭一个会话时
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        // 用户连接关闭时，执行注销操作
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            String agentId = (String) session.getAttributes().get("agentId");
            if (agentId != null) {
                WebSocketSession agentSession = AgentManager.getAgentSession(agentId);
                if (agentSession != null && agentSession.isOpen()) {
                    WSResultUtils.sendMessage(agentSession, new ServerMessage("customer_disconnect", "用户已断开连接"));
                }
                AgentManager.unbindUserAgent(userId, agentId);
            } else {
                AgentManager.exitWaitingQueue(session.getId());
            }
        }
        AgentManager.removeUserSession(session.getId());
    }

    //处理传输错误时
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // 处理传输过程中出现的异常
        log.error("客服连接异常，Session ID: {}, 错误信息: {}", session.getId(), exception.getMessage());
        AgentManager.agentLogout(session.getId());  // 客服注销
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws Exception {
        ClientMessage clientMsg = JSONUtil.toBean(message.getPayload(), ClientMessage.class);
        if (clientMsg == null || clientMsg.getType() == null) {
            WSResultUtils.sendError(session, "消息格式错误");
            return;
        }
        if (StringUtils.isBlank(clientMsg.getContent())) {
            WSResultUtils.sendError(session, "消息内容不能为空");
            return;
        }
        //用户未登录时，不允许发送消息，只允许连接初始化
        if (!Objects.equals(clientMsg.getType(), MessageType.INIT) && session.getAttributes().get("userId") == null) {
            // throw new WebSocketException(session, "请登录");
        }
        switch (clientMsg.getType()) {
            case MessageType.INIT -> handleInit(session, clientMsg); // 用户连接初始化
            case MessageType.MESSAGE -> handleMessage(session, clientMsg); // 用户发送消息
            case MessageType.TRANSFER -> handleTransfer(session); //转人工
            case MessageType.AGENT_STATUS -> handleStatusRequest(session); // 查询客服状态
            case MessageType.CANCEL_TRANSFER -> AgentManager.exitWaitingQueue(session.getId()); // 取消排队
            default -> WSResultUtils.sendError(session, "未知消息类型");
        }
    }

    //初始化连接：验证用户身份
    private void handleInit(WebSocketSession session, ClientMessage msg) {
        Integer userId = userService.tokenToUid(msg.getToken());
        if (userId == null || userId == -1) {
            WSResultUtils.sendError(session, "身份验证失败");
            return;
        }
        session.getAttributes().put("userId", userId);
        WSResultUtils.sendMessage(session, new ServerMessage("init_success", "连接初始化成功"));
    }

    //处理用户消息
    private void handleMessage(WebSocketSession session, ClientMessage msg) {
        String agentId =  AgentManager.getAgentForUser(session.getId());
        if (agentId != null) {
            WebSocketSession agentSession = AgentManager.getAgentSession(agentId);
            if (agentSession != null && agentSession.isOpen()) {
                WSResultUtils.sendMessage(agentSession, new ServerMessage("customer_message", msg.getContent()));
                return;
            }
            WSResultUtils.sendError(session, "客服未连接");
        }
        //调用AI接口
        String aiResponse = aiManager.doSyncRequest(Knowledge.KNOWLEDGE, msg.getContent());
        aiResponse = aiResponse.replace("\n", "").replace(" ", "");
        //解析json
        int start = aiResponse.indexOf("{");
        int end = aiResponse.lastIndexOf("}");
        String json = aiResponse.substring(start, end + 1);
        AIResponse res = JSONUtil.toBean(json, AIResponse.class);
        WSResultUtils.sendMessage(session, new ServerMessage("ai_response", res.getMessage()));
    }

    //处理转人工请求
    private void handleTransfer(WebSocketSession session) {
        if (AgentManager.getOnlineAgentCount() == 0) {
            WSResultUtils.sendError(session, "当前无客服在线，无法转人工");
            return;
        }
        if (session.getAttributes().containsKey("agentId")) {
            WSResultUtils.sendError(session, "您已分配到客服，无需重复转人工");
            return;
        }

        AgentManager.enterWaitingQueue(session.getId());
        WSResultUtils.sendMessage(session, new ServerMessage("transfer_queued", "已加入排队队列，前方排队人数：" + AgentManager.getWaitingQueuePosition(session.getId())));
    }

    // 查询客服状态
    private void handleStatusRequest(WebSocketSession session) {
        Map<String, Object> status = new HashMap<>();
        status.put("onlineAgents", AgentManager.getOnlineAgentCount());
        status.put("waitingCount", AgentManager.getWaitingQueuePosition(session.getId()));
        WSResultUtils.sendMessage(session, new ServerMessage("agent_status", status));
    }
}
