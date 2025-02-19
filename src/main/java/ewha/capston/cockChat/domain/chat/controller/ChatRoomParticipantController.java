package ewha.capston.cockChat.domain.chat.controller;

import ewha.capston.cockChat.domain.chat.dto.ChatRoomResponseDto;
import ewha.capston.cockChat.domain.chat.dto.ChatRoomSettingRequestDto;
import ewha.capston.cockChat.domain.chat.dto.ChatRoomSettingResponseDto;
import ewha.capston.cockChat.domain.chat.service.ChatRoomParticipantService;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.participant.dto.OwnerRequestDto;
import ewha.capston.cockChat.domain.participant.dto.ParticipantResponseDto;
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

    /* 채팅방 설정 */
    @PutMapping("/{chatRoomId}/settings")
    public ResponseEntity<ChatRoomSettingResponseDto> updateSettings(@AuthUser Member member, @PathVariable(name = "chatRoomId") Long chatRoomId
    , @RequestBody ChatRoomSettingRequestDto requestDto){
        return chatRoomParticipantService.updateSettings(member,chatRoomId,requestDto);
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

}
