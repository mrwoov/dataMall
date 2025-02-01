package com.dataMall.customerServiceCenter.manager;

import com.dataMall.customerServiceCenter.model.AgentStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class AgentManager {
    private static final ConcurrentHashMap<String, AgentStatus> onlineAgents = new ConcurrentHashMap<>();
    // 保存用户ID与客服ID的绑定关系
    private static final ConcurrentHashMap<String, String> userToAgent = new ConcurrentHashMap<>();
    // 保存客服ID与用户ID的绑定关系
    private static final ConcurrentHashMap<String, String> agentToUser = new ConcurrentHashMap<>();
    // 另外还需要保存用户在线的会话，这里假设有个用户会话管理，可以通过 UserSessionManager 获取
    private static final ConcurrentHashMap<String, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();
    //排队等待人工客服的用户队列
    private static final LinkedBlockingQueue<String> WaitingQueue = new LinkedBlockingQueue<>();

    public static void agentLogin(WebSocketSession session) {
        onlineAgents.put(session.getId(), new AgentStatus(session, AgentStatus.Status.IDLE));
    }

    public static void agentLogout(String sessionId) {
        onlineAgents.remove(sessionId);
        // 当客服退出时，清除绑定关系
        String userId = agentToUser.remove(sessionId);
        if (userId != null) {
            userToAgent.remove(userId);
        }
    }

    public static int getOnlineAgentCount() {
        return onlineAgents.size();
    }

    public static WebSocketSession getAgentSession(String agentId) {
        AgentStatus agentStatus = onlineAgents.get(agentId);
        return agentStatus != null ? agentStatus.getSession() : null;
    }

    // 获取当前客服绑定的用户ID
    public static String getUserForAgent(String agentId) {
        return agentToUser.get(agentId);
    }

    // 获取当前用户绑定的客服ID
    public static String getAgentForUser(String userId) {
        return userToAgent.get(userId);
    }

    // 绑定用户与客服
    public static void bindUserAgent(String userId, String agentId) {
        userToAgent.put(userId, agentId);
        agentToUser.put(agentId, userId);
    }

    // 解绑用户与客服
    public static void unbindUserAgent(String userId, String agentId) {
        userToAgent.remove(userId);
        agentToUser.remove(agentId);
    }

    // 从等待队列中分配下一个用户并建立绑定关系
    public static String assignNextUser(String agentId) {
        String userId = WaitingQueue.poll();
        if (userId != null) {
            bindUserAgent(userId, agentId);
        }
        return userId;
    }

    //用户当前排队位次
    public static long getWaitingQueuePosition(String userId) {
        // 获取用户在队列中的位置
        return WaitingQueue.size() - WaitingQueue.stream().takeWhile(id -> !id.equals(userId)).count();
    }

    //排队进入等待队列
    public static void enterWaitingQueue(String userId) {
        WaitingQueue.offer(userId);
    }

    //退出等待队列
    public static void exitWaitingQueue(String userId) {
        WaitingQueue.removeIf(id -> id.equals(userId));
    }

    // 在线用户会话的获取方法
    public static WebSocketSession getUserSession(String userId) {
        return onlineUsers.get(userId);
    }

    // 当用户登录时，需要保存用户会话到 onlineUsers
    public static void addUserSession(String userId, WebSocketSession session) {
        onlineUsers.put(userId, session);
    }

    // 当用户退出时，移除用户会话
    public static void removeUserSession(String userId) {
        onlineUsers.remove(userId);
    }
}
