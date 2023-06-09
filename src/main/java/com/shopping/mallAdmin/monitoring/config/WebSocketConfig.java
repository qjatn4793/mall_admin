package com.shopping.mallAdmin.monitoring.config;

import com.shopping.mallAdmin.setting.service.SettingService;
import com.shopping.mallAdmin.monitoring.controller.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Autowired
    SettingService settingService;

    private WebSocketHandlerRegistry registry;

    //@Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        this.registry = registry;
        registerHandlers();
    }

    public void registerHandlers() {
        registry.addHandler(webSocketHandler, "/webSocket/**")
        .setAllowedOrigins("http://59.12.242.207:8888", "http://172.30.1.30:81");
    }
}