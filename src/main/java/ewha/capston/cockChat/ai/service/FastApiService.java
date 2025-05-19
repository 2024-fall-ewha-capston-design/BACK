package ewha.capston.cockChat.ai.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ewha.capston.cockChat.ai.dto.ChatAnalysisRequestDto;
import ewha.capston.cockChat.ai.dto.ChatAnalysisResult;
import ewha.capston.cockChat.ai.dto.NegativeChatRequestDto;
import ewha.capston.cockChat.domain.notification.domain.Notification;
import ewha.capston.cockChat.domain.notification.repository.MongoNotificationRepository;
import ewha.capston.cockChat.domain.penalty.service.PenaltyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class FastApiService {
    private final WebClient webClient;

    @Autowired
    private  PenaltyService penaltyService;

    @Autowired
    private MongoNotificationRepository mongoNotificationRepository;

    public FastApiService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8000").build();
    }

    // 기본 생성자 (필요한 경우 추가)
    public FastApiService() {
        this.webClient = WebClient.create("http://127.0.0.1:8000");
    }

    private final RestTemplate restTemplate = new RestTemplate();

    /* 사용자 맞춤 알림 설정 */




    /* 요주의 인물 처리 gpt call */
    public void  filterNegativeChat(ChatAnalysisRequestDto requestDto) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(requestDto);
        System.out.println("📤 보낼 JSON:\n" + jsonBody);
        try {
            // 1️⃣ FastAPI로 요청 전송
            List<ChatAnalysisResult> analysisResults = webClient.post()
                    .uri("/filter_negative_chat")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ChatAnalysisResult>>() {})
                    .timeout(Duration.ofSeconds(10))
                    .block();

            //System.out.println("📌 분석 결과: " + analysisResults);

            // 2️⃣ 응답이 비어있으면 종료
            if (analysisResults == null || analysisResults.isEmpty()) {
                log.info("분석 결과 없음 - 패널티 생성 생략");
                return;
            }

            /* 패털티 부여 */
            penaltyService.applyPenaltiesFromAnalysis(analysisResults);


            /*
            // 4️⃣ MongoDB에 저장
            List<Notification> savedNotifications = mongoNotificationRepository.saveAll(notifications);
            System.out.println("✅ 저장된 알림 개수: " + savedNotifications.size());

*/
            //log.info("총 {}개의 알림이 저장되었습니다.", notifications.size());

        } catch (Exception e) {
            log.error("패널티 부여 중 오류 발생: {}", e.getMessage(), e);
        }
    }

        /*
        try {
            Object object = webClient.post()
                    .uri("/filter_negative_chat")
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(object);

        } catch (Exception e) {
            /* 응답 생성에 문제 생긴 경우, 에러 메시지 반환
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "gpt 응답 생성에 문제가 발생했습니다.", "exception", e.getMessage()));
        }
         */


    /* 사용자 맞춤 알림 생성 */
    public void analyzeChatAndMakeNotification(ChatAnalysisRequestDto requestDto) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(requestDto);
        System.out.println("📤 보낼 JSON:\n" + jsonBody);
        try {
            // 1️⃣ FastAPI로 요청 전송
            List<ChatAnalysisResult> analysisResults = webClient.post()
                    .uri("/analyze_chat")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ChatAnalysisResult>>() {})
                    .timeout(Duration.ofSeconds(10))
                    .block();

            //System.out.println("📌 분석 결과: " + analysisResults);

            // 2️⃣ 응답이 비어있으면 종료
            if (analysisResults == null || analysisResults.isEmpty()) {
                log.info("분석 결과 없음 - 알림 생성 생략");
                return;
            }

            // 3️⃣ MongoDB에 저장할 Notification 객체 생성
            List<Notification> notifications = analysisResults.stream()
                    .map(result -> new Notification(
                           // UUID.randomUUID().toString(), // MongoDB 저장용 ID
                            result.getChatId(),
                            Long.valueOf(result.getKeywordId()),
                            Long.valueOf(result.getParticipantId()),
                            false
                    ))
                    .collect(Collectors.toList());

            // 4️⃣ MongoDB에 저장
            List<Notification> savedNotifications = mongoNotificationRepository.saveAll(notifications);
            System.out.println("✅ 저장된 알림 개수: " + savedNotifications.size());


            log.info("총 {}개의 알림이 저장되었습니다.", notifications.size());

        } catch (Exception e) {
            log.error("사용자 맞춤 알림 생성 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
