package com.example.practice_backend.service;

import com.example.practice_backend.model.MeasurementData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * 웹 클라이언트에 데이터를 브로드캐스트하는 서비스
 */
@Service
public class BroadcastService {
    
    private static final Logger logger = LoggerFactory.getLogger(BroadcastService.class);
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public BroadcastService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    /**
     * 모든 웹 클라이언트에 측정 데이터를 브로드캐스트
     * 
     * @param data 측정 데이터
     */
    public void broadcastMeasurement(MeasurementData data) {
        logger.info("Broadcasting measurement data: {}", data);
        
        // /topic/measurements 경로를 구독하는 모든 클라이언트에게 브로드캐스트
        messagingTemplate.convertAndSend("/topic/measurements", data);
    }
    
    /**
     * 특정 측정코드로 브로드캐스트
     * 
     * @param measurementCode 측정코드
     * @param data 측정 데이터
     */
    public void broadcastByMeasurementCode(String measurementCode, MeasurementData data) {
        logger.info("Broadcasting measurement data for code {}: {}", measurementCode, data);
        
        // 측정코드별로 별도의 토픽으로 브로드캐스트
        messagingTemplate.convertAndSend("/topic/measurements/" + measurementCode, data);
    }
    
    /**
     * 특정 기기 ID로 브로드캐스트
     * 
     * @param deviceId 기기 ID
     * @param data 측정 데이터
     */
    public void broadcastByDeviceId(String deviceId, MeasurementData data) {
        logger.info("Broadcasting measurement data for device {}: {}", deviceId, data);
        
        // 기기별로 별도의 토픽으로 브로드캐스트
        messagingTemplate.convertAndSend("/topic/device/" + deviceId, data);
    }
}

