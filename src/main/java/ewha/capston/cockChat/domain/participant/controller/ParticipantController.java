package ewha.capston.cockChat.domain.participant.controller;

import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.member.dto.request.MemberUpdateRequestDto;
import ewha.capston.cockChat.domain.participant.dto.ParticipantRealRequestDto;
import ewha.capston.cockChat.domain.participant.dto.ParticipantAnonymousRequestDto;
import ewha.capston.cockChat.domain.participant.dto.ParticipantResponseDto;
import ewha.capston.cockChat.domain.participant.service.ParticipantService;
import ewha.capston.cockChat.global.config.auth.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ParticipantController {

    private final ParticipantService participantService;

    /* 신규 실명 채팅방 입장 */
    @PostMapping("/chatRooms/{chatRoomId}/participants/real")
    public ResponseEntity<ParticipantResponseDto> joinRealNameChatroom(@AuthUser Member member,  @PathVariable Long chatRoomId, @RequestBody ParticipantRealRequestDto requestDto){
        return participantService.joinRealNameChatroom(member,chatRoomId, requestDto);
    }

    /* 신규 익명 채팅방 입장 */
    @PostMapping("/chatRooms/{chatRoomId}/participants/anonymous")
    public ResponseEntity<ParticipantResponseDto> joinAnonymousChatroom(@AuthUser Member member, @PathVariable Long chatRoomId, @RequestBody ParticipantAnonymousRequestDto requestDto){
        return participantService.joinAnonymousChatroom(member,chatRoomId,requestDto);
    }

    /* 익명 프로필 수정 */
    @PutMapping("/participants/{participantId}/anonymous")
    public ResponseEntity<ParticipantResponseDto> updateAnonymousProfile(@AuthUser Member member, @PathVariable Long participantId, @RequestBody MemberUpdateRequestDto requestDto){
        return participantService.updateAnonymousProfile(member, participantId, requestDto);
    }

    /* 프로필 조회 */
    @GetMapping("/participants/{participantId}")
    public ResponseEntity<ParticipantResponseDto> getParticipant(@AuthUser Member member, @PathVariable Long participantId){
        return participantService.getParticipant(member, participantId);
    }

}
