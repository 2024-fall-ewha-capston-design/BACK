package ewha.capston.cockChat.domain.chat.controller;

import ewha.capston.cockChat.domain.chat.dto.ChatMessageRequestDto;
import ewha.capston.cockChat.domain.chat.dto.ChatResponseDto;
import ewha.capston.cockChat.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /* 채팅 보내기 */
    @MessageMapping("/chat/send")
    public void sendMessage(@Payload  ChatMessageRequestDto requestDto){
        log.info("Received message request: {}", requestDto);
        chatService.sendMessage(requestDto.getRoomId(), requestDto);
    }

    /* 채팅 내역 조회 */
    @GetMapping("/chats/{chatRoomId}/messages")
    public ResponseEntity<List<ChatResponseDto>> getChatMessageList(@PathVariable(name = "chatRoomId") Long chatRoomId){
        return chatService.getChatMessageList(chatRoomId);
    }
}
