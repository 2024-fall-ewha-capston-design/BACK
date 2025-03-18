package ewha.capston.cockChat.global.config.webSocket;

import ewha.capston.cockChat.domain.member.service.JwtTokenProvider;
import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.participant.dto.ParticipantResponseDto;
import ewha.capston.cockChat.domain.participant.repository.ParticipantRepository;
import ewha.capston.cockChat.global.exception.CustomException;
import ewha.capston.cockChat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Map;

import static org.hibernate.boot.model.source.internal.hbm.Helper.getValue;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    // private final SimpMessageSendingOperations messagingTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final SimpMessagingTemplate messagingTemplate;
    private final ParticipantRepository participantRepository;

    /* connect 요청 */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event){
        logger.info("Received a new web socket connection ");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Long memberId = Long.valueOf((String) accessor.getSessionAttributes().get("memberId"));
        accessor.setHeader("memberId", memberId.toString());
        accessor.setNativeHeader("memberId", String.valueOf(memberId));
        System.out.println("✅ WebSocket 연결: memberId 전송 완료 -> " + memberId);
    }


    /* subscribe 요청(입장) */
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event){
        logger.info("Received a new web socket subscribe");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        String destination = accessor.getDestination();
        if (destination != null && destination.startsWith("/topic/public/")) {
            Long memberId = Long.valueOf((String) sessionAttributes.get("memberId"));
            if (memberId == null) {
                logger.warn("⚠️ memberId가 세션에 없음!");
                return;
            }

            Long roomId = Long.valueOf((String) sessionAttributes.get("chatRoomId"));
            Long participantId = Long.valueOf((String) sessionAttributes.get("participantId"));
            Participant participant = participantRepository.findById(participantId)
                    .orElseThrow(()->new CustomException(ErrorCode.INVALID_PARTICIPANT));

            String sessionId = accessor.getSessionId();  // 현재 사용자의 세션 ID

            // 해당 세션을 가진 유저에게만 participantId 전송
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/participant-info", ParticipantResponseDto.of(participant));

            logger.info("✅ participantId={}를 세션 {}의 사용자에게 전송", participantId, sessionId);
        }
        //String
    }
}
