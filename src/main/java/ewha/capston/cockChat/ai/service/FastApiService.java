package ewha.capston.cockChat.ai.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import ewha.capston.cockChat.ai.dto.NegativeChatRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class FastApiService {
    private final WebClient webClient;

    public FastApiService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8000").build();
    }

    // 기본 생성자 (필요한 경우 추가)
    public FastApiService() {
        this.webClient = WebClient.create("http://127.0.0.1:8000");
        //this.webClient = WebClient.create(); -> connection refused
    }

    private final RestTemplate restTemplate = new RestTemplate();

    /* 요주의 인물 처리 gpt call */
    public ResponseEntity<Object> filterNegativeChat(NegativeChatRequestDto requestDto) {
        try{
            Object object= webClient.post()
                    .uri("/filter_negative_chat")
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(object);

        }catch (Exception e){
            /* 응답 생성에 문제 생긴 경우, 에러 메시지 반환 */
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "gpt 응답 생성에 문제가 발생했습니다.", "exception", e.getMessage()));
        }
    }
}
