package com.dataMall.customerServiceCenter.model;

import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

/**
 * 排队等待转人工的请求数据结构，记录用户会话 ID、用户 ID 及请求创建时间。
 */
@Data
public class TransferRequest {
    private final String sessionId;
    private final Integer userId;
    private final long createTime;

    public TransferRequest(WebSocketSession session) {
        this.sessionId = session.getId();
        Object uid = session.getAttributes().get("userId");
        this.userId = uid instanceof Integer ? (Integer) uid : null;
        this.createTime = System.currentTimeMillis();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferRequest that = (TransferRequest) o;
        return Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }
}
