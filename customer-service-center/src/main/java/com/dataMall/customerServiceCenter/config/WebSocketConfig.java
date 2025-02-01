package com.dataMall.customerServiceCenter.config;

import com.dataMall.customerServiceCenter.websocket.AgentWebSocketHandler;
import com.dataMall.customerServiceCenter.websocket.CustomerWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customerWebSocketHandler(), "/ws/customer")
                .setAllowedOrigins("*");
        registry.addHandler(agentWebSocketHandler(), "/ws/agent")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler customerWebSocketHandler() {
        return new CustomerWebSocketHandler();
    }

    @Bean
    public WebSocketHandler agentWebSocketHandler() {
        return new AgentWebSocketHandler();
    }
}
