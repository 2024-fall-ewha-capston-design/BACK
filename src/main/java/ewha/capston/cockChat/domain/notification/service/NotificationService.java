package ewha.capston.cockChat.domain.notification.service;

import ewha.capston.cockChat.ai.dto.ChatAnalysisRequestDto;
import ewha.capston.cockChat.ai.service.FastApiService;
import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import ewha.capston.cockChat.domain.chat.repository.ChatRoomRepository;
import ewha.capston.cockChat.domain.chat.service.ChatRoomService;
import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.participant.domain.ParticipantPositiveKeyword;
import ewha.capston.cockChat.domain.participant.repository.ParticipantPositiveKeywordRepository;
import ewha.capston.cockChat.domain.participant.repository.ParticipantRepository;
import ewha.capston.cockChat.domain.participant.service.ParticipantService;
import ewha.capston.cockChat.global.exception.CustomException;
import ewha.capston.cockChat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FastApiService fastApiService;
    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;
    private final ParticipantPositiveKeywordRepository positiveKeywordRepository;
    private final ChatRoomService chatRoomService;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final int MESSAGE_THRESHOLD = 3; // 임계값

    /* Redis에서 메시지 카운트 증가 및 분석 트리거 */
    public void incrementMessageCount(Long roomId, String messageId, Long senderId, String messageContent) {
        String countKey = "chatroom:" + roomId + ":message_count";
        String recentKey = "chatroom:" + roomId + ":recent_messages";

        /* 메시지 개수 증가 */
        Long count = redisTemplate.opsForValue().increment(countKey);
        log.info("Room {} - 메시지 개수: {}", roomId, count);

        /* (메시지 ID | 보낸 사람 ID | 메시지 내용) 형태로 Redis 저장 */
        String messageData = messageId + "|" + senderId + "|" + messageContent;
        redisTemplate.opsForList().rightPush(recentKey, messageData);

        /* 최대 10개 유지 (FIFO) */
        if (redisTemplate.opsForList().size(recentKey) > 10) {
            redisTemplate.opsForList().leftPop(recentKey);
        }

        /* 메시지 임계값 도달 시 분석 트리거 */
        if (count != null && count >= MESSAGE_THRESHOLD) {
            saveFixedMessageSnapshot(roomId);
            analyzeChatAsync(roomId);
        }
    }

    /* 임계값 초과 시점의 최근 10개 메시지 저장 */
    private void saveFixedMessageSnapshot(Long roomId) {
        String recentKey = "chatroom:" + roomId + ":recent_messages";
        String fixedKey = "chatroom:" + roomId + ":fixed_messages";

        List<String> recentMessages = redisTemplate.opsForList().range(recentKey, 0, -1);
        if (recentMessages != null && !recentMessages.isEmpty()) {
            redisTemplate.delete(fixedKey); // 기존 데이터 삭제
            redisTemplate.opsForList().rightPushAll(fixedKey, recentMessages);
            log.info("Room {} - 임계점 초과 시점의 메시지 10개 저장 완료", roomId);
        }
    }

    /* OpenAI 분석 실행 */
    @Async
    public void analyzeChatAsync(Long roomId) {
        String fixedKey = "chatroom:" + roomId + ":fixed_messages";
        List<String> fixedMessages = redisTemplate.opsForList().range(fixedKey, 0, -1);
        if (fixedMessages == null || fixedMessages.isEmpty()) return;

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ROOM));
        List<Participant> participants = participantRepository.findAllByChatRoomAndIsActiveTrue(chatRoom);

        // 사용자별 키워드 매핑
        Map<String, List<String>> participantKeywords = participants.stream()
                .collect(Collectors.toMap(
                        p -> String.valueOf(p.getParticipantId()),  // ✅ Long → String 변환 보장
                        p -> positiveKeywordRepository.findAllByParticipant(p).stream()
                                .map(ParticipantPositiveKeyword::getContent)
                                .collect(Collectors.toList())
                ));

        // 메시지와 키워드를 하나의 요청 객체로 변환
        ChatAnalysisRequestDto requestDto = new ChatAnalysisRequestDto(fixedMessages, participantKeywords);

        // OpenAI 분석 요청 (FastAPI 서비스 호출)
        fastApiService.analyzeChatAndMakeNotification(requestDto);

        //notifications.forEach(notification -> saveNotification(notification));
        redisTemplate.delete("chatroom:" + roomId + ":message_count");
    }
    // redis 카운트 값 초기화하 는 부분 필요함.
}
