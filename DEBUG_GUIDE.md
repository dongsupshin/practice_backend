# WebSocket 메시지 수신 문제 디버깅 가이드

## 문제 상황
- 의료 기기 시뮬레이터와 웹 클라이언트는 모두 연결 성공
- 데이터 전송 로그는 보이지만 웹 클라이언트에서 메시지 수신 안 됨

## 확인 사항

### 1. 서버 콘솔 로그 확인

서버를 실행한 터미널/콘솔에서 다음 로그를 확인하세요:

**정상적인 경우 보이는 로그:**
```
INFO  - Received measurement data from device
INFO  - Broadcasting measurement data: MeasurementData{...}
```

**로그가 보이지 않으면:**
- 서버가 메시지를 받지 못하고 있는 것입니다
- 아래 항목들을 확인하세요

### 2. 서버 실행 방법

```bash
# Gradle로 실행
./gradlew :practice-backend-app:bootRun

# 또는 빌드 후 실행
./gradlew :practice-backend-app:build
java -jar practice-backend-app/build/libs/practice-backend-app-*.war
```

### 3. 서버 로그 레벨 확인

`application.properties`에 로깅 설정이 추가되었습니다:
```properties
logging.level.com.example.practice_backend.websocket=DEBUG
logging.level.com.example.practice_backend.service=DEBUG
logging.level.org.springframework.messaging=DEBUG
logging.level.org.springframework.web.socket=DEBUG
```

이 설정으로 더 자세한 로그가 출력됩니다.

### 4. 브라우저 개발자 도구 확인

1. F12로 개발자 도구 열기
2. Console 탭에서 에러 메시지 확인
3. Network 탭에서 WebSocket 연결 확인
   - `ws://localhost:8080/ws/device` 연결 상태
   - `ws://localhost:8080/ws/client` 연결 상태

### 5. 서버 포트 확인

서버가 8080 포트에서 실행 중인지 확인:
- `application.properties`에 `server.port=8080` 설정
- 다른 애플리케이션이 8080 포트를 사용하고 있지 않은지 확인

### 6. 가능한 원인 및 해결 방법

#### 원인 1: 컨트롤러가 스캔되지 않음
- `PracticeBackendApplication`의 `scanBasePackages`에 `com.example.practice_backend`가 포함되어 있는지 확인
- 현재 설정: `@SpringBootApplication(scanBasePackages = {"com.example.practice_backend", "com.example.helloworld"})`
- ✅ 정상

#### 원인 2: JSON 변환 문제
- Spring Boot는 자동으로 Jackson을 포함하므로 문제 없어야 함
- 서버 로그에서 JSON 파싱 에러 확인

#### 원인 3: WebSocket 연결 문제
- 브라우저에서 WebSocket 연결이 실제로 성공했는지 확인
- 개발자 도구 Network 탭에서 확인

#### 원인 4: 메시지 경로 불일치
- 클라이언트 전송 경로: `/app/device/measurement`
- 서버 매핑 경로: `@MessageMapping("/device/measurement")`
- ✅ 정상 (prefix `/app` 제외하고 매핑)

### 7. 테스트 단계

1. **서버 재시작**
   ```bash
   ./gradlew :practice-backend-app:bootRun
   ```

2. **서버 로그 확인**
   - 서버 시작 시 WebSocket 설정 관련 로그 확인
   - "WebSocketConfig" 또는 "WebSocketMessageBrokerConfigurer" 관련 로그 확인

3. **클라이언트 연결**
   - 브라우저에서 `http://localhost:8080/test-client.html` 접속
   - 의료 기기 연결 클릭
   - 웹 클라이언트 연결 클릭

4. **데이터 전송**
   - 데이터 전송 버튼 클릭
   - 서버 콘솔 로그 확인

5. **서버 로그 확인**
   - "Received measurement data from device" 로그가 있는지 확인
   - "Broadcasting measurement data" 로그가 있는지 확인

### 8. 추가 디버깅

서버 로그에 아무것도 나타나지 않는다면:

1. **서버가 실행 중인지 확인**
   ```bash
   # Windows
   netstat -ano | findstr :8080
   
   # Linux/Mac
   lsof -i :8080
   ```

2. **서버 재빌드**
   ```bash
   ./gradlew clean build
   ./gradlew :practice-backend-app:bootRun
   ```

3. **브라우저 캐시 클리어**
   - Ctrl+Shift+R (하드 리프레시)

### 9. 정상 작동 시 예상 로그

서버 콘솔:
```
INFO  c.e.p.w.DeviceWebSocketController - === Received measurement data from device ===
INFO  c.e.p.w.DeviceWebSocketController - Data: MeasurementData{measurementCode='BP001', ...}
INFO  c.e.p.s.BroadcastService - Broadcasting measurement data: MeasurementData{...}
INFO  c.e.p.w.DeviceWebSocketController - === Successfully processed measurement data ===
```

클라이언트 로그 (브라우저 콘솔):
```
[시간] 모든 측정 데이터 수신: {"measurementCode":"BP001",...}
```

---

**문제가 지속되면 서버 콘솔의 전체 로그를 확인하여 추가 디버깅을 진행하세요.**

