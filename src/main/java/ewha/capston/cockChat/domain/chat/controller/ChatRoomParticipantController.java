package ewha.capston.cockChat.domain.chat.controller;

import ewha.capston.cockChat.domain.chat.dto.ChatRoomResponseDto;
import ewha.capston.cockChat.domain.chat.service.ChatRoomParticipantService;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.global.config.auth.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatRooms")
public class ChatRoomParticipantController {

    private final ChatRoomParticipantService chatRoomParticipantService;

    /* 사용자가 참여중인 모든 채팅방 조회 */
    @GetMapping("/my")
    public ResponseEntity<List<ChatRoomResponseDto>> getChatRoomsByMember(@AuthUser Member member){
        return chatRoomParticipantService.getChatRoomsByMember(member);
    }

    /* 채팅방 탈퇴 */
    @DeleteMapping("/{chatRoomId}/participants/me")
    public ResponseEntity<Void> removeParticipantFromChatRoom(@AuthUser Member member, @PathVariable Long chatRoomId){
        return chatRoomParticipantService.removeParticipantFromChatRoom(member, chatRoomId);
    }
}
