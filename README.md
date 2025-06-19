# 🧠 Chatcipe - 실시간 오픈채팅 기반 사용자 맞춤형 알림 서비스

Chatcipe는 실시간 다대다 채팅을 분석하여 **사용자 맞춤 알림(Notification)** 을 제공하는 졸업 프로젝트입니다.  
오픈 채팅을 기반으로 **AI가 주요 발언자 감지**, **요주의 인물 알림**, **문맥 기반 분석** 기능을 제공합니다.

본 저장소는 Chatcipe의 **Spring Boot 기반 백엔드 서버**를 포함합니다.

---

## 📦 주요 기능 및 구성 모듈

### ✅ 사용자 맞춤 알림 기능
- Redis에 임시 저장된 채팅 메시지를 일정량 수집 후 FastAPI로 전달
- FastAPI는 메시지를 분석하고, 키워드 기반 사용자 맞춤 알림을 반환
- Spring Boot는 분석 결과를 MongoDB에 저장

📁 관련 코드:
- `NotificationService.java` : 알림 저장 및 조회 서비스
- `FastApiService.java` : Redis 메시지 전달 및 응답 처리
- `KeywordAnalysisRequestDto.java` : 메시지 분석 요청 DTO

---

### ✅ 요주의 인물 실시간 알림
- MongoDB에 저장된 메시지를 바탕으로 상위 발언자를 3초마다 선정
- WebSocket + STOMP를 이용하여 **키워드 설정 사용자에게 실시간 전송**

📁 관련 코드:
- `ChatAnalysisTriggerService.java` : 상위 발언자 주기적 계산
- `PenaltyService.java` : WebSocket 전송 로직

---

### ✅ 다대다 실시간 채팅 (WebSocket + STOMP 기반)
- 사용자 인증 기반의 다대다 익명 채팅
- 메시지는 Redis에 저장 후 분석용으로 사용됨

📁 관련 코드:
- `ChatController.java` : 메시지 송수신 처리
- `RedisChatRepository.java` : Redis 연동
- `WebSocketConfig.java` : WebSocket/STOMP 설정

---

## 🛠 Build & Run

### 💻 요구사항
- Java 17+
- Gradle 7+
- MongoDB & Redis 설치 (로컬 또는 EC2)
- Python 기반 FastAPI 서버 (내부 연동용)

### 🔨 빌드

```bash
./gradlew clean build
```

### 🚀 실행

```bash
java -jar build/libs/BACK-0.0.1-SNAPSHOT.jar
```

또는 로컬 개발용:

```bash
./gradlew bootRun
```

---

## 📥 설치 방법

1. `.env` 혹은 `application.yml`에 환경변수 입력:
   - MongoDB URI
   - Redis Host/Port
   - FastAPI 내부 엔드포인트 주소

2. MongoDB와 Redis가 실행 중인지 확인
3. 위 명령어로 서버 실행

---

## 🧪 테스트 방법

```bash
./gradlew test
```

Spring Boot 기반의 단위/통합 테스트 코드가 `src/test/` 경로에 포함되어 있습니다.  
기본 기능(채팅 저장, 알림 응답, API 통신 등)에 대한 테스트가 구현되어 있습니다.

---

## 🧰 기술 스택

- **Spring Boot**
- **WebSocket + STOMP**
- **MongoDB / Redis**
- **FastAPI (Python)** – 메시지 분석용 내부 서버
- **OpenAI GPT-4o API** – 분석 로직 내 활용
- **GitHub Actions + AWS EC2** – CI/CD 환경

---

## 📂 프로젝트 구조

```
📦 BACK
 ┣ 📂 config         // WebSocket, Redis, MongoDB 설정
 ┣ 📂 controller     // WebSocket 및 REST API 엔드포인트
 ┣ 📂 domain
 ┃ ┣ 📂 chat         // 채팅 도메인
 ┃ ┣ 📂 notification // 알림 도메인
 ┣ 📂 service        // 핵심 서비스 로직
 ┣ 📂 dto            // 요청/응답 DTO
 ┣ 📂 scheduler      // 주기적 분석 작업
 ┣ 📂 util           // 유틸 함수들
 ┣ 📜 application.yml
 ┗ 📜 build.gradle
```

---

## 📌 기타 참고 사항

- FastAPI 서버는 같은 EC2 인스턴스 내에서 실행되며, 외부 접근은 차단됨
- 모든 데이터 흐름은 내부 API를 통해 암호화되어 처리됨
- WebSocket 통신은 인증 기반으로 구성되어 있음 (JWT + STOMP)

---

## 📮 문의

해당 프로젝트에 대한 문의사항은 팀 GitHub 이슈 또는 담당자에게 직접 연락 바랍니다.
