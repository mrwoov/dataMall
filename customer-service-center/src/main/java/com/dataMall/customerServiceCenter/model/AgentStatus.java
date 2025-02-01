package com.dataMall.customerServiceCenter.model;

import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

/**
 * 表示客服（人工客服）的状态信息，包括与客服对应的 WebSocketSession 以及当前状态（空闲、忙碌、离线）。
 */
@Data
public class AgentStatus {
    private final WebSocketSession session;
    private Status status;

    public AgentStatus(WebSocketSession session, Status status) {
        this.session = session;
        this.status = status;
    }

    public enum Status {
        IDLE("空闲", 1),
        BUSY("忙碌", 2),
        OFFLINE("离线", 3);

        private final String name;
        private final int value;

        Status(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        public static Status valueOf(int value) {
            for (Status status : values()) {
                if (status.value == value) {
                    return status;
                }
            }
            return null;
        }
    }
}
