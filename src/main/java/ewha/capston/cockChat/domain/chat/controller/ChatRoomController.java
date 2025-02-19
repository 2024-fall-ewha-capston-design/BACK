package ewha.capston.cockChat.domain.chat.controller;

import ewha.capston.cockChat.domain.chat.dto.ChatRoomRequestDto;
import ewha.capston.cockChat.domain.chat.dto.ChatRoomResponseDto;
import ewha.capston.cockChat.domain.chat.service.ChatRoomService;
import ewha.capston.cockChat.domain.chat.service.ChatService;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.member.service.MemberService;
import ewha.capston.cockChat.global.config.auth.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ChatRoomController {

    private final ChatService chatService;
    private final MemberService memberService;
    private final ChatRoomService chatRoomService;

    /* 채팅방 생성 */
    // 이후  @AuthUser 추가하기
    @PostMapping("/chatRooms")
    public ResponseEntity<ChatRoomResponseDto> createChatRoom( @AuthUser Member member, @RequestBody ChatRoomRequestDto requestDto){
        return chatRoomService.createChatRoom(requestDto, member);
    }

    /* 고유 코드로 채팅방 조회 */
    @GetMapping("/chatRooms/{code}")
    public ResponseEntity<ChatRoomResponseDto> getChatRoomByIdentifier(@AuthUser Member member, @PathVariable String code){
        return chatRoomService.getChatRoomByIdentifier(member,code);
    }

    /* 채팅방 이름으로 채팅방 목록 조회 */
    @GetMapping("/chatRooms")
    public ResponseEntity<List<ChatRoomResponseDto>> getChatRoomListByRoomName(@AuthUser Member member, @RequestParam String roomName){
        return chatRoomService.getChatRoomListByRoomName(roomName);
    }

    /* 채팅방 삭제 */
    @DeleteMapping("/chatRooms/{chatRoomId}")
    public ResponseEntity<Void> removeChatRoom(@AuthUser Member member, @PathVariable(name = "chatRoomId") Long chatRoomId){
        return chatRoomService.removeChatRoom(member,chatRoomId);
    }

    /* mongoDB 테스트 */
    @PostMapping("/chat/test")
    public ResponseEntity testMongo(){
        chatService.mongoDBConnectionTest();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("성공");
    }
}
