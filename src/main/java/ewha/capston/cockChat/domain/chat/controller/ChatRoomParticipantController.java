package ewha.capston.cockChat.domain.chat.controller;

import ewha.capston.cockChat.domain.chat.dto.response.ChatRoomResponseDto;
import ewha.capston.cockChat.domain.chat.service.ChatRoomParticipantService;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.participant.dto.request.OwnerRequestDto;
import ewha.capston.cockChat.domain.participant.dto.response.ParticipantResponseDto;
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

    /* 채팅방에 참여할 participant 조회 */
    @GetMapping("/{chatRoomId}/participants")
    public ResponseEntity<ParticipantResponseDto> getParticipantByMemberAndChatRoom(@AuthUser Member member, @PathVariable(name = "chatRoomId") Long chatRoomId){
        return chatRoomParticipantService.getParticipantByMemberAndChatRoom(member,chatRoomId);
    }

    /* 채팅방 방장 변경 */
    @PutMapping("/{chatRoomId}/owner")
    public ResponseEntity<ParticipantResponseDto> updateChatRoomOwner(@AuthUser Member member, @PathVariable(name = "chatRoomId") Long chatRoomId, @RequestBody OwnerRequestDto requestDto){
        return chatRoomParticipantService.updateOwner(member, chatRoomId, requestDto);
    }

    /* 채팅방 탈퇴 */
    @DeleteMapping("/{chatRoomId}/participants/me")
    public ResponseEntity<Void> removeParticipantFromChatRoom(@AuthUser Member member, @PathVariable(name = "chatRoomId") Long chatRoomId){
        return chatRoomParticipantService.removeParticipantFromChatRoom(member, chatRoomId);
    }

    /* 방장 권한 참여자 탈퇴 */
    @DeleteMapping("/{chatRoomId}/participants/{participantId}")
    public ResponseEntity<Void> removeParticipantByOwner(@AuthUser Member member, @PathVariable(name = "chatRoomId") Long chatRoomId, @PathVariable(name = "participantId") Long participantId){
        return chatRoomParticipantService.removeParticipantByOwner(member,chatRoomId,participantId);
    }

}
