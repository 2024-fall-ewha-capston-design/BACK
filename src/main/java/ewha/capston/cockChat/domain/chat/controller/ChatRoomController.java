package ewha.capston.cockChat.domain.chat.controller;

import ewha.capston.cockChat.domain.chat.dto.ChatRoomRequestDto;
import ewha.capston.cockChat.domain.chat.dto.ChatRoomResponseDto;
import ewha.capston.cockChat.domain.chat.service.ChatService;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ChatRoomController {

    private final ChatService chatService;
    private final MemberService memberService;

    /* 채팅방 생성 */
    // 이후  @AuthUser 추가하기
    @PostMapping("/chatRoom")
    public ResponseEntity<ChatRoomResponseDto> createChatRoom(@RequestBody ChatRoomRequestDto requestDto){
        Member member = memberService.findMemberByEmail("ahtnwl1004@ewhain.net");
        return chatService.createChatRoom(requestDto, member);
    }

    /* mongoDB 테스트 */
    @PostMapping("/chat/test")
    public ResponseEntity testMongo(){
        chatService.mongoDBConnectionTest();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("성공");
    }
}
