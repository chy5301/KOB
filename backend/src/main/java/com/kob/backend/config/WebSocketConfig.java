package com.kob.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocketConfig类用于配置WebSocket服务器端点。
 */
@Configuration
public class WebSocketConfig {

    /**
     * serverEndpointExporter方法用于创建并返回一个ServerEndpointExporter对象。
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        // ServerEndpointExporter是一个用于将服务器端点暴露给WebSocket客户端的Bean。
        return new ServerEndpointExporter();
    }
}

