package com.example.practice_backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
public class WebSocketEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("=== WebSocket Connection Established ===");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        logger.info("Session ID: {}", headerAccessor.getSessionId());
        logger.info("User: {}", headerAccessor.getUser());
    }
    
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        logger.info("=== WebSocket Connection Disconnected ===");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        logger.info("Session ID: {}", headerAccessor.getSessionId());
    }
    
    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        logger.info("=== WebSocket Subscribe ===");
        logger.info("Session ID: {}", headerAccessor.getSessionId());
        logger.info("Subscription ID: {}", headerAccessor.getSubscriptionId());
        logger.info("Destination: {}", headerAccessor.getDestination());
    }
    
    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        logger.info("=== WebSocket Unsubscribe ===");
        logger.info("Session ID: {}", headerAccessor.getSessionId());
        logger.info("Subscription ID: {}", headerAccessor.getSubscriptionId());
    }
}

