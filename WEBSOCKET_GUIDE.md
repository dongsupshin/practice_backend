# WebSocket 기반 의료 기기 실시간 데이터 브로드캐스트 가이드

## 아키텍처 개요

```
의료 기기 장비  --[WebSocket]-->  서버  --[WebSocket Broadcast]-->  웹 클라이언트들
   (데이터 전송)                      (중계/브로드캐스트)              (실시간 수신)
```

## 프로젝트 구조

```
practice-backend-app/
├── config/
│   └── WebSocketConfig.java          # WebSocket 설정
├── model/
│   └── MeasurementData.java          # 측정 데이터 모델
├── service/
│   └── BroadcastService.java         # 브로드캐스트 서비스
└── websocket/
    └── DeviceWebSocketController.java # 의료 기기 메시지 처리
```

## 주요 구성 요소

### 1. WebSocketConfig
- WebSocket 메시징 브로커 설정
- STOMP 엔드포인트 등록
  - `/ws/device`: 의료 기기용 엔드포인트
  - `/ws/client`: 웹 클라이언트용 엔드포인트

### 2. MeasurementData
- 측정 데이터 모델
- 필드: measurementCode, deviceId, value, unit, timestamp

### 3. DeviceWebSocketController
- 의료 기기에서 전송된 데이터 수신
- `/app/device/measurement` 경로로 메시지 수신

### 4. BroadcastService
- 웹 클라이언트에 데이터 브로드캐스트
- 브로드캐스트 경로:
  - `/topic/measurements`: 모든 측정 데이터
  - `/topic/measurements/{measurementCode}`: 측정코드별
  - `/topic/device/{deviceId}`: 기기별

## 사용 방법

### 1. 서버 실행
```bash
./gradlew bootRun
```

### 2. 의료 기기에서 데이터 전송

의료 기기는 WebSocket을 통해 `/ws/device`에 연결하고, `/app/device/measurement`로 데이터를 전송합니다.

#### JavaScript 예시 (의료 기기 시뮬레이션):
```javascript
// SockJS 및 STOMP 라이브러리 필요
const socket = new SockJS('http://localhost:8080/ws/device');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // 측정 데이터 전송
    const measurementData = {
        measurementCode: 'BP001',  // 혈압 측정코드 예시
        deviceId: 'DEVICE-001',
        value: 120.5,
        unit: 'mmHg',
        timestamp: new Date().toISOString()
    };
    
    stompClient.send('/app/device/measurement', {}, JSON.stringify(measurementData));
});
```

#### Java 예시 (의료 기기 클라이언트):
```java
// Spring STOMP 클라이언트 사용
@Configuration
public class DeviceClientConfig {
    @Bean
    public WebSocketStompClient stompClient() {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        return stompClient;
    }
}
```

### 3. 웹 클라이언트에서 데이터 수신

웹 클라이언트는 `/ws/client`에 연결하고, `/topic/measurements`를 구독합니다.

#### JavaScript 예시 (웹 클라이언트):
```javascript
const socket = new SockJS('http://localhost:8080/ws/client');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // 모든 측정 데이터 구독
    stompClient.subscribe('/topic/measurements', function(data) {
        const measurement = JSON.parse(data.body);
        console.log('Received measurement:', measurement);
        // UI 업데이트 로직
        updateUI(measurement);
    });
    
    // 특정 측정코드 구독
    stompClient.subscribe('/topic/measurements/BP001', function(data) {
        const measurement = JSON.parse(data.body);
        console.log('Received BP001 measurement:', measurement);
    });
    
    // 특정 기기 구독
    stompClient.subscribe('/topic/device/DEVICE-001', function(data) {
        const measurement = JSON.parse(data.body);
        console.log('Received device data:', measurement);
    });
});

function updateUI(measurement) {
    // UI 업데이트 로직
    document.getElementById('measurement-code').textContent = measurement.measurementCode;
    document.getElementById('value').textContent = measurement.value + ' ' + measurement.unit;
    document.getElementById('timestamp').textContent = measurement.timestamp;
}
```

#### HTML 예시:
```html
<!DOCTYPE html>
<html>
<head>
    <title>의료 기기 실시간 모니터링</title>
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/stompjs@2/lib/stomp.min.js"></script>
</head>
<body>
    <h1>실시간 측정 데이터</h1>
    <div id="measurement-code"></div>
    <div id="value"></div>
    <div id="timestamp"></div>
    
    <script>
        // 위의 JavaScript 코드
    </script>
</body>
</html>
```

## 데이터 흐름

1. **의료 기기 → 서버**
   - 의료 기기가 `/ws/device`에 WebSocket 연결
   - `/app/device/measurement` 경로로 `MeasurementData` JSON 전송

2. **서버 처리**
   - `DeviceWebSocketController`가 메시지 수신
   - 데이터 유효성 검사
   - `BroadcastService`를 통해 브로드캐스트

3. **서버 → 웹 클라이언트**
   - `/topic/measurements`를 구독하는 모든 클라이언트에 브로드캐스트
   - 선택적으로 측정코드별, 기기별 브로드캐스트

## 보안 고려사항

1. **인증/인가**
   - 의료 기기 인증 추가 (예: API 키, JWT)
   - 웹 클라이언트 인증 추가

2. **CORS 설정**
   - 현재는 `setAllowedOriginPatterns("*")`로 모든 출처 허용
   - 운영 환경에서는 특정 도메인만 허용하도록 수정

3. **데이터 검증**
   - 측정 데이터 유효성 검사 강화
   - 이상치 감지 및 필터링

## 확장 가능한 기능

1. **데이터베이스 저장**
   - 측정 데이터를 DB에 저장하는 서비스 추가

2. **알림 시스템**
   - 임계값 초과 시 알림 발송

3. **기기 관리**
   - 기기 등록/해제 관리
   - 기기 상태 모니터링

4. **로드 밸런싱**
   - 여러 서버 인스턴스 간 메시지 동기화 (Redis Pub/Sub 등)

## 테스트

### cURL로 WebSocket 테스트 (예시):
```bash
# WebSocket 연결 테스트는 cURL로 직접 어려우므로, 
# 브라우저 콘솔이나 Postman의 WebSocket 기능 사용 권장
```

### 단위 테스트 예시:
```java
@SpringBootTest
@AutoConfigureMockMvc
class DeviceWebSocketControllerTest {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Test
    void testMeasurementBroadcast() {
        MeasurementData data = new MeasurementData("BP001", "DEVICE-001", 120.5, "mmHg");
        // 테스트 로직
    }
}
```

## 참고 자료

- [Spring WebSocket Documentation](https://docs.spring.io/spring-framework/reference/web/websocket.html)
- [STOMP Protocol Specification](https://stomp.github.io/)
- [SockJS Documentation](https://github.com/sockjs/sockjs-client)

