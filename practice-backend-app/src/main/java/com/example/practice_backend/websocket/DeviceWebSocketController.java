package com.example.practice_backend.websocket;

import com.example.practice_backend.model.MeasurementData;
import com.example.practice_backend.service.BroadcastService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * 의료 기기에서 전송되는 WebSocket 메시지를 처리하는 컨트롤러
 */
@Controller
public class DeviceWebSocketController {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceWebSocketController.class);
    
    private final BroadcastService broadcastService;
    
    public DeviceWebSocketController(BroadcastService broadcastService) {
        this.broadcastService = broadcastService;
    }
    
    /**
     * 의료 기기에서 측정 데이터를 전송받아 웹 클라이언트에 브로드캐스트
     * 
     * 클라이언트는 /app/device/measurement 경로로 메시지 전송
     * 
     * @param data 측정 데이터
     */
    @MessageMapping("/device/measurement")
    public void handleMeasurement(@Payload MeasurementData data) {
        logger.info("=== Received measurement data from device ===");
        logger.info("Data: {}", data);
        logger.info("MeasurementCode: {}", data != null ? data.getMeasurementCode() : "null");
        logger.info("DeviceId: {}", data != null ? data.getDeviceId() : "null");
        logger.info("Value: {}", data != null ? data.getValue() : "null");
        
        try {
            // 데이터 유효성 검사
            if (data == null) {
                logger.error("Received null measurement data");
                return;
            }
            
            if (data.getMeasurementCode() == null || data.getMeasurementCode().isEmpty()) {
                logger.warn("Invalid measurement data: measurementCode is null or empty");
                return;
            }
            
            logger.info("Broadcasting measurement data to web clients...");
            
            // 모든 웹 클라이언트에 브로드캐스트
            broadcastService.broadcastMeasurement(data);
            
            // 측정코드별로도 브로드캐스트 (선택사항)
            broadcastService.broadcastByMeasurementCode(data.getMeasurementCode(), data);
            
            logger.info("=== Successfully processed measurement data ===");
            
        } catch (Exception e) {
            logger.error("Error processing measurement data: {}", e.getMessage(), e);
            logger.error("Exception stack trace:", e);
        }
    }
}

