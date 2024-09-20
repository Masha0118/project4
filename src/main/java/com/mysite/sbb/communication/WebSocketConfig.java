package com.mysite.sbb.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);


    @Value("${app.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        logger.info("Registering WebSocket endpoints");
        registry.addEndpoint("/wss")
                .setAllowedOrigins(allowedOrigins.split(","))
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        logger.info("Configuring message broker");
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }
}
