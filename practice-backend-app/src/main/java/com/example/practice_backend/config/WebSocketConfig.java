package com.example.practice_backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 구독할 수 있는 브로드캐스트 경로 설정
        config.enableSimpleBroker("/topic", "/queue");
        // 클라이언트가 메시지를 보낼 때 사용할 prefix
        config.setApplicationDestinationPrefixes("/app");
        logger.info("Message broker configured - prefixes: /app, destinations: /topic, /queue");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        logger.info("Registering WebSocket endpoints...");
        
        // 의료 기기용 WebSocket 엔드포인트
        registry.addEndpoint("/ws/device")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        logger.info("Registered endpoint: /ws/device");
        
        // 웹 클라이언트용 WebSocket 엔드포인트
        registry.addEndpoint("/ws/client")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        logger.info("Registered endpoint: /ws/client");
    }
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null) {
                    StompCommand command = accessor.getCommand();
                    if (command != null) {
                        logger.info("=== Inbound Message ===");
                        logger.info("Command: {}", command);
                        logger.info("Destination: {}", accessor.getDestination());
                        logger.info("Session ID: {}", accessor.getSessionId());
                        if (command == StompCommand.SEND) {
                            Object payload = message.getPayload();
                            if (payload instanceof byte[]) {
                                logger.info("Message body (bytes): {}", new String((byte[]) payload));
                            } else {
                                logger.info("Message body: {}", payload);
                            }
                        }
                    }
                }
                return message;
            }
        });
    }
}
