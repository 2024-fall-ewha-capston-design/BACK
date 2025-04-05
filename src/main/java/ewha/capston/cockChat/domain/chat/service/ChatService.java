package ewha.capston.cockChat.domain.chat.service;

import ewha.capston.cockChat.domain.chat.domain.Chat;
import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import ewha.capston.cockChat.domain.chat.dto.reqeust.ChatMessageRequestDto;
import ewha.capston.cockChat.domain.chat.dto.response.ChatResponseDto;
import ewha.capston.cockChat.domain.chat.mongo.MongoChatRepository;
import ewha.capston.cockChat.domain.chat.repository.ChatRoomRepository;
import ewha.capston.cockChat.domain.notification.service.NotificationService;
import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.participant.repository.ParticipantRepository;
import ewha.capston.cockChat.global.exception.CustomException;
import ewha.capston.cockChat.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    @Autowired
    private MongoChatRepository mongoChatRepository;

    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final NotificationService notificationService;

    /* mongoDB 연결 확인 */
    /*
    public void mongoDBConnectionTest(){
        Chat chat = new Chat(1L, 1L, "test", MessageType.CHAT);
        mongoChatRepository.save(chat);
    }

     */


    /* 메시지 보내기 */
    public void sendMessage(Long roomId, ChatMessageRequestDto requestDto) {

        /* 채팅 내용으로 공백이 들어온 경우, 예외 발생 */
        if(requestDto.getContent().isBlank()) throw new CustomException(ErrorCode.INVALID_MESSAGE_CONTENT);

        /* roomId 검증 */
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_ROOM));

        /* participantId 검증 */
        Participant sender = participantRepository.findById(requestDto.getSenderId())
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PARTICIPANT));

        /* 채팅 mongoDB 저장 */
        Chat chat = mongoChatRepository.save(Chat.builder()
                .chatroomId(roomId)
                .participantId(requestDto.getSenderId())
                .message(requestDto.getContent())
                .chatType(requestDto.getType())
                .build()
        );

        ChatResponseDto responseDto = ChatResponseDto.of(chat,sender);

        /* 메시지 송신 */
        messagingTemplate.convertAndSend("/topic/public/" + roomId, responseDto);

        /* 메시지 개수 증가 및 OpenAI 분석 트리거  : 일단 주석 처리 */
        //notificationService.incrementMessageCount(roomId, chat.getId() ,chat.getParticipantId(), requestDto.getContent());
    }

    /* 채팅 내역 조회 */
    public ResponseEntity<List<ChatResponseDto>> getChatMessageList(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_ROOM));
        List<Chat> chatList = mongoChatRepository.findAllByChatroomIdOrderByCreatedDateAsc(chatRoomId);

        List<Participant> senderList = participantRepository.findAllByChatRoom(chatRoom);
        Map<Long, Participant> senderMap = senderList.stream().collect(Collectors.toMap(Participant::getParticipantId, Function.identity()));

        return ResponseEntity.status(HttpStatus.OK)
                .body(chatList.stream().map(
                        chat -> {
                            Participant sender = senderMap.get(chat.getParticipantId());
                            return ChatResponseDto.of(chat,sender);
                        }).collect(Collectors.toList()));

        /*
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatList.stream().map(ChatResponseDto::of).collect(Collectors.toList()));

         */
    }
}
