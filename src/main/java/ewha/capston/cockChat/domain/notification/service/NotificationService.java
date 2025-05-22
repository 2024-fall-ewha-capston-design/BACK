package ewha.capston.cockChat.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ewha.capston.cockChat.ai.dto.ChatAnalysisRequestDto;
import ewha.capston.cockChat.ai.dto.KeywordAnalysisRequestDto;
import ewha.capston.cockChat.ai.service.FastApiService;
import ewha.capston.cockChat.domain.chat.domain.Chat;
import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import ewha.capston.cockChat.domain.chat.mongo.MongoChatRepository;
import ewha.capston.cockChat.domain.chat.repository.ChatRoomRepository;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.notification.domain.Notification;
import ewha.capston.cockChat.domain.notification.dto.NotificationResponseDto;
import ewha.capston.cockChat.domain.notification.repository.MongoNotificationRepository;
import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.participant.domain.ParticipantPositiveKeyword;
import ewha.capston.cockChat.domain.participant.repository.ParticipantPositiveKeywordRepository;
import ewha.capston.cockChat.domain.participant.repository.ParticipantRepository;
import ewha.capston.cockChat.global.exception.CustomException;
import ewha.capston.cockChat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FastApiService fastApiService;

    private final ParticipantRepository participantRepository;
    private final ParticipantPositiveKeywordRepository positiveKeywordRepository;
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

        /* 최대 10개 유지 (FIFO) - 일단 이 부분 주석처리해보고 .. */
        /*
        if (redisTemplate.opsForList().size(recentKey) > MESSAGE_THRESHOLD) {
            redisTemplate.opsForList().leftPop(recentKey);
        }
         */

        /* 메시지 임계값 도달 시 분석 트리거 */
        if (count != null && count >= MESSAGE_THRESHOLD) {
            List<String> messagesForAnalysis = extractMessagesForAnalysis(roomId);
            analyzeChatAsync(roomId, messagesForAnalysis);
            //redisTemplate.delete(countKey);

            //saveFixedMessageSnapshot(roomId);
            //analyzeChatAsync(roomId);
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

        /* gpt1
        String recentKey = "chatroom:" + roomId + ":recent_messages";

        // 앞에서부터 N개 메시지 순서 보존하며 가져오기
        List<String> recentMessages = redisTemplate.opsForList().range(recentKey, 0, MESSAGE_THRESHOLD - 1);
        if (recentMessages == null) recentMessages = new ArrayList<>();

        for (int i = 0; i < MESSAGE_THRESHOLD; i++) {
            String message = redisTemplate.opsForList().leftPop(recentKey);
            /*
            if (message != null) {
                extractedMessages.add(message);
            }

        }

        log.info("Room {} - 분석용 메시지 {}개 추출 완료", roomId, recentMessages.size());
        return recentMessages;

         */

        /* 이게 원래 버전
        String recentKey = "chatroom:" + roomId + ":recent_messages";

        List<String> extractedMessages = new ArrayList<>();
        for (int i = 0; i < MESSAGE_THRESHOLD; i++) {
            String message = redisTemplate.opsForList().leftPop(recentKey);
            if (message != null) {
                extractedMessages.add(message);
            }
        }

        log.info("Room {} - 분석용 메시지 {}개 추출 완료", roomId, extractedMessages.size());
        return extractedMessages;

         */
    }

    /* 임계값 초과 시점의 최근 10개 메시지 저장 */
    /*
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

     */

    /* OpenAI 분석 실행 */
    @Async
    public void analyzeChatAsync(Long roomId, List<String> messages) throws JsonProcessingException {
        String fixedKey = "chatroom:" + roomId + ":fixed_messages";
        //List<String> fixedMessages = redisTemplate.opsForList().range(fixedKey, 0, -1);
        if (messages == null || messages.isEmpty()) return;

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ROOM));
        List<Participant> participants = participantRepository.findAllByChatRoomAndIsActiveTrue(chatRoom);

        // 사용자별 키워드 매핑
        Map<String, List<KeywordAnalysisRequestDto>> participantKeywords = participants.stream()
                .collect(Collectors.toMap(
                        p -> String.valueOf(p.getParticipantId()), // Long → String
                        p -> positiveKeywordRepository.findAllByParticipant(p).stream()
                                .map(k -> new KeywordAnalysisRequestDto(String.valueOf(k.getPositiveKeywordId()), k.getContent()))
                                .collect(Collectors.toList())
                ));

        // 메시지와 키워드를 하나의 요청 객체로 변환
        ChatAnalysisRequestDto requestDto = new ChatAnalysisRequestDto(messages, participantKeywords);

        // OpenAI 분석 요청 (FastAPI 서비스 호출)
        fastApiService.analyzeChatAndMakeNotification(requestDto);

        redisTemplate.delete("chatroom:" + roomId + ":message_count");
    }
    // redis 카운트 값 초기화하 는 부분 필요함.


    /* 알림 목록 조회 */
    public ResponseEntity<List<NotificationResponseDto>> getMemberNotificationList(Member member) {
        List<Participant> participantList = participantRepository.findAllByMemberAndIsActiveTrue(member);
        List<Long> participantIdList = participantList.stream().map(Participant::getParticipantId).toList();
        if(participantIdList.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(null);

        List<Notification> notificationList = notificationRepository.findByParticipantIdInOrderByCreatedDateDesc(participantIdList);

        // 본인 알림은 제외
        for(Notification notification : notificationList){
            Chat chat = chatRepository.findById(notification.getChatId()).orElseThrow(()-> new CustomException(ErrorCode.INVALID_CHAT_ID));
            Participant chatSender = participantRepository.findById(chat.getParticipantId()).orElseThrow(()->new CustomException(ErrorCode.INVALID_PARTICIPANT));
            if(chatSender.getMember().getMemberId().equals(member.getMemberId())){
                notificationList.remove(notification);
                notificationRepository.delete(notification);
            }
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(notificationList.stream()
                        .map(notification -> {
                            Chat chat = chatRepository.findById(notification.getChatId()).orElseThrow();
                            ChatRoom chatRoom = chatRoomRepository.findById(chat.getChatroomId())
                                    .orElseThrow(()->new CustomException(ErrorCode.INVALID_ROOM));
                            ParticipantPositiveKeyword keyword = positiveKeywordRepository.findById(notification.getKeywordId()).orElseThrow();
                            boolean isRead = notification.isRead(); // 또는 다른 로직
                            return NotificationResponseDto.of(notification, chat, chatRoom, keyword, isRead);
                        })
                        .collect(Collectors.toList()));
    }

    /* 알림 읽음 처리 */
    public ResponseEntity<Object> markNotificationAsRead(Member member, String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_NOTIFICATION));
        Participant participant = participantRepository.findById(notification.getParticipantId())
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PARTICIPANT));
        if(!participant.getMember().equals(member)) throw new CustomException(ErrorCode.NO_PERMISSION);
        notificationRepository.delete(notification);

        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }
}
