package ewha.capston.cockChat.domain.chat.controller;

import ewha.capston.cockChat.domain.chat.dto.ChatRoomRequestDto;
import ewha.capston.cockChat.domain.chat.dto.ChatRoomResponseDto;
import ewha.capston.cockChat.domain.chat.service.ChatService;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.member.service.MemberService;
import ewha.capston.cockChat.global.config.auth.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ChatRoomController {

    private final ChatService chatService;
    private final MemberService memberService;

    /* 채팅방 생성 */
    // 이후  @AuthUser 추가하기
    @PostMapping("/chatRooms")
    public ResponseEntity<ChatRoomResponseDto> createChatRoom( @AuthUser Member member, @RequestBody ChatRoomRequestDto requestDto){
        return chatService.createChatRoom(requestDto, member);
    }

    /* 고유 코드로 채팅방 조회 */
    @GetMapping("/chatRooms/{code}")
    public ResponseEntity<ChatRoomResponseDto> getChatRoomByIdentifier(@AuthUser Member member, @PathVariable String code){
        return chatService.getChatRoomByIdentifier(member,code);
    }

    /* mongoDB 테스트 */
    @PostMapping("/chat/test")
    public ResponseEntity testMongo(){
        chatService.mongoDBConnectionTest();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("성공");
    }
}
