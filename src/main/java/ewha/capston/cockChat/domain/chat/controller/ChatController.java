package ewha.capston.cockChat.domain.chat.controller;

import ewha.capston.cockChat.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/test")
    public ResponseEntity testMongo(){
        chatService.mongoDBConnectionTest();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("성공");
    }

}
