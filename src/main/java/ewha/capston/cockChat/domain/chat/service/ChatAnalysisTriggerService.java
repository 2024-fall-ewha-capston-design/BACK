package ewha.capston.cockChat.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ewha.capston.cockChat.ai.dto.ChatAnalysisRequestDto;
import ewha.capston.cockChat.ai.dto.KeywordAnalysisRequestDto;
import ewha.capston.cockChat.ai.service.FastApiService;
import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import ewha.capston.cockChat.domain.chat.mongo.MongoChatRepository;
import ewha.capston.cockChat.domain.chat.repository.ChatRoomRepository;
import ewha.capston.cockChat.domain.notification.repository.MongoNotificationRepository;
import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.participant.domain.ParticipantNegativeKeyword;
import ewha.capston.cockChat.domain.participant.repository.ParticipantNegativeKeywordRepository;
import ewha.capston.cockChat.domain.participant.repository.ParticipantPositiveKeywordRepository;
import ewha.capston.cockChat.domain.participant.repository.ParticipantRepository;
import ewha.capston.cockChat.global.exception.CustomException;
import ewha.capston.cockChat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatAnalysisTriggerService {
    private final FastApiService fastApiService;

    private final ParticipantRepository participantRepository;
    private final ParticipantPositiveKeywordRepository positiveKeywordRepository;
    private final ParticipantNegativeKeywordRepository negativeKeywordRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MongoChatRepository chatRepository;
    private final MongoNotificationRepository notificationRepository;

    private final RedisTemplate<String, String> redisTemplate;

    private static final int MESSAGE_THRESHOLD = 3; // 임계값

    /* Redis에서 메시지 카운트 증가 및 분석 트리거 */
    public void incrementMessageCount(Long roomId, String messageId, Long senderId, String messageContent) throws JsonProcessingException {
        String countKey = "chatroom:" + roomId + ":message_count";
        String recentKey = "chatroom:" + roomId + ":recent_messages";

        /* 메시지 개수 증가 */
        Long count = redisTemplate.opsForValue().increment(countKey);
        log.info("Room {} - 메시지 개수: {}", roomId, count);

        /* (메시지 ID | 보낸 사람 ID | 메시지 내용) 형태로 Redis 저장 */
        String messageData = messageId + "|" + senderId + "|" + messageContent;
        redisTemplate.opsForList().rightPush(recentKey, messageData);

        /* 메시지 임계값 도달 시 분석 트리거 */
        if (count != null && count >= MESSAGE_THRESHOLD) {
            List<String> messagesForAnalysis = extractMessagesForAnalysis(roomId);
            analyzeChatAsync(roomId, messagesForAnalysis);
        }
    }


    /* 임계치만큼 메시지 추출 후 삭제 */
    private List<String> extractMessagesForAnalysis(Long roomId) {
        String recentKey = "chatroom:" + roomId + ":recent_messages";

        // 앞에서부터 N개 메시지 순서 보존하며 가져오기
        List<String> recentMessages = redisTemplate.opsForList().range(recentKey, 0, MESSAGE_THRESHOLD - 1);

        // 메시지가 부족한 경우 추가 메시지를 가져오기
        if (recentMessages == null || recentMessages.size() < MESSAGE_THRESHOLD) {
            recentMessages = new ArrayList<>();
        }

        // 추출된 메시지 삭제
        for (int i = 0; i < MESSAGE_THRESHOLD && !recentMessages.isEmpty(); i++) {
            redisTemplate.opsForList().leftPop(recentKey);
        }

        log.info("Room {} - 분석용 메시지 {}개 추출 완료", roomId, recentMessages.size());
        return recentMessages;


    }

    @Async
    public void analyzeChatAsync(Long roomId, List<String> messages) throws JsonProcessingException {
        String fixedKey = "chatroom:" + roomId + ":fixed_messages";
        if (messages == null || messages.isEmpty()) return;

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ROOM));
        List<Participant> participants = participantRepository.findAllByChatRoomAndIsActiveTrue(chatRoom);


        /* 사용자 맞춤 알림 생성 */
        // 사용자별 키워드 매핑
        Map<String, List<KeywordAnalysisRequestDto>> participantKeywords = participants.stream()
                .collect(Collectors.toMap(
                        p -> String.valueOf(p.getParticipantId()), // Long → String
                        p -> positiveKeywordRepository.findAllByParticipant(p).stream()
                                .map(k -> new KeywordAnalysisRequestDto(String.valueOf(k.getPositiveKeywordId()), k.getContent()))
                                .collect(Collectors.toList())
                ));

        // 메시지와 키워드를 하나의 요청 객체로 변환
        ChatAnalysisRequestDto positiveKeywordRequestDto = new ChatAnalysisRequestDto(messages, participantKeywords);

        // OpenAI 분석 요청 (FastAPI 서비스 호출) : 사용자 맞춤 알림 생성
        fastApiService.analyzeChatAndMakeNotification(positiveKeywordRequestDto);




        /* 요주으 인물 처리 */
        Map<String, List<KeywordAnalysisRequestDto>> participantNegativeKeywords = participants.stream()
                        .collect(Collectors.toMap(
                                p-> String.valueOf(p.getParticipantId()),
                                p-> negativeKeywordRepository.findAllByParticipant(p).stream()
                                        .map(k-> new KeywordAnalysisRequestDto(String.valueOf(k.getNegativeKeywordId()), k.getContent()))
                                        .collect(Collectors.toList())
                        ));
        ChatAnalysisRequestDto negativeKeywordRequestDto = new ChatAnalysisRequestDto(messages, participantNegativeKeywords);
        fastApiService.filterNegativeChat(negativeKeywordRequestDto);


        redisTemplate.delete("chatroom:" + roomId + ":message_count");
    }


}
