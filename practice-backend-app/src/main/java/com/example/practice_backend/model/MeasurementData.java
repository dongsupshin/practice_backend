package com.example.practice_backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

/**
 * 의료 기기에서 전송되는 측정 데이터 모델
 */
public class MeasurementData {
    
    private String measurementCode;  // 측정코드
    private String deviceId;         // 기기 ID
    private Double value;            // 측정값
    private String unit;             // 단위
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp; // 측정 시각 (UTC)
    
    // 기본 생성자
    public MeasurementData() {
        this.timestamp = Instant.now();
    }
    
    // 생성자
    public MeasurementData(String measurementCode, String deviceId, Double value, String unit) {
        this.measurementCode = measurementCode;
        this.deviceId = deviceId;
        this.value = value;
        this.unit = unit;
        this.timestamp = Instant.now();
    }
    
    // Getters and Setters
    public String getMeasurementCode() {
        return measurementCode;
    }
    
    public void setMeasurementCode(String measurementCode) {
        this.measurementCode = measurementCode;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public Double getValue() {
        return value;
    }
    
    public void setValue(Double value) {
        this.value = value;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "MeasurementData{" +
                "measurementCode='" + measurementCode + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

