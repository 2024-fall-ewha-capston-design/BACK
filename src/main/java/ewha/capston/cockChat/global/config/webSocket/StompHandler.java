package ewha.capston.cockChat.global.config.webSocket;

import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import ewha.capston.cockChat.domain.chat.repository.ChatRoomRepository;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.member.repository.MemberRepository;
import ewha.capston.cockChat.domain.member.service.JwtTokenProvider;
import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.participant.repository.ParticipantRepository;
import ewha.capston.cockChat.global.exception.CustomException;
import ewha.capston.cockChat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    public static final String DEFAULT_PATH = "/topic/public/";

    //private final SimpMessagingTemplate messagingTemplate;

    private final JwtTokenProvider jwtTokenProvider;
    private final ParticipantRepository participantRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    /* webSocket을 통해 들어온 요청 처리 전 실행됨 */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        log.info("📌 WebSocket 요청 감지 - Command: {}", command);

        /* webSocket 연결 요청인 경우 */
        if(StompCommand.CONNECT.equals(command)){

            /* 토큰 검증 */
            Long memberId = jwtTokenProvider.getMemberIdFromToken(accessor.getFirstNativeHeader("Authorization"));

            if(memberId == null) {
                log.error("🚨 memberId를 추출할 수 없음! (토큰 문제 가능)");
                throw new CustomException(ErrorCode.INVALID_PARTICIPANT);
            }

            log.info("✅ WebSocket 연결됨 - memberId: {}", memberId);

            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            sessionAttributes.put("memberId", memberId.toString());
            accessor.setSessionAttributes(sessionAttributes);
            accessor.setNativeHeader("memberId", String.valueOf(memberId)); // 클라이언트에게 전달

            // 🔥 세션에 정상적으로 저장됐는지 바로 확인
            log.info("🔍 StompHandler에서 설정한 세션 값: {}", accessor.getSessionAttributes().get("memberId"));
        }

        /* 채팅방 구독 요청인 경우 */
        else if(StompCommand.SUBSCRIBE.equals(command)){
            handleSubscribe(accessor);
        }

        /* 웹소켓 연결 종료 요청인 경우 */
        else if(StompCommand.DISCONNECT.equals(command)){
            Long participantId = Long.valueOf((String) accessor.getSessionAttributes().get("participantId"));
            log.info("DISCONNECTED participantId : {}" , participantId);
        }

        log.info("header : " + message.getHeaders());
        log.info("message:" + message);

        return message;
    }

    /* 구독 처리 */
    private void  handleSubscribe(StompHeaderAccessor accessor){
        String destination = accessor.getDestination();
        Long memberId = Long.valueOf((String) accessor.getSessionAttributes().get("memberId"));

        if (destination != null && destination.startsWith("/topic/public/")){
            Long chatRoomId = parseChatRoomIdFromPath(accessor);

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(()->new CustomException(ErrorCode.NO_MEMBER_EXIST));
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                    .orElseThrow(()->new CustomException(ErrorCode.INVALID_ROOM));

            Participant participant = participantRepository.findByMemberAndChatRoomAndIsActiveTrue(member,chatRoom)
                    .orElseThrow(()->new CustomException(ErrorCode.NOT_A_PARTICIPANT));

            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            sessionAttributes.put("chatRoomId", chatRoom.getRoomId().toString());
            sessionAttributes.put("participantId", participant.getParticipantId().toString());

            log.info("✅ 구독 성공! - participantId: {}", accessor.getSessionAttributes().get("participantId"));
        }
    }


    /* 구독 경로에서 채팅방 id 검출 */
    private Long parseChatRoomIdFromPath(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        String roomIdStr = destination.replaceAll(".*/public/", "");
        Long roomId = Long.valueOf(roomIdStr);
        return roomId;
    }


    private Long verifyAccessToken(String accessToken){
        if(!jwtTokenProvider.validateToken(accessToken)){
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        return jwtTokenProvider.getMemberIdFromToken(accessToken);
    }

    private void setValue(StompHeaderAccessor accessor, String key, Object value) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        sessionAttributes.put(key, value);
    }

    private Object getValue(StompHeaderAccessor accessor, String key) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        Object value = sessionAttributes.get(key);

        if (Objects.isNull(value)) {
            throw new CustomException(ErrorCode.INPUT_IS_NULL);
        }
        return value;
    }

    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (Objects.isNull(sessionAttributes)) {
            throw new CustomException(ErrorCode.SESSION_ATTRIBUTE_IS_NULL);
        }
        return sessionAttributes;
    }
}
