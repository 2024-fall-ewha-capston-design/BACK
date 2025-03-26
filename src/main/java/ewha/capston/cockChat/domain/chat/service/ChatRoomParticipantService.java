package ewha.capston.cockChat.domain.chat.service;

import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import ewha.capston.cockChat.domain.chat.dto.response.ChatRoomResponseDto;
import ewha.capston.cockChat.domain.chat.repository.ChatRoomRepository;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.participant.dto.OwnerRequestDto;
import ewha.capston.cockChat.domain.participant.dto.ParticipantResponseDto;
import ewha.capston.cockChat.domain.participant.repository.ParticipantRepository;
import ewha.capston.cockChat.global.exception.CustomException;
import ewha.capston.cockChat.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomParticipantService {

    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;

    private final ChatRoomService chatRoomService;

    /* 참여 중인 모든 채팅방 목록 조회 */
    public ResponseEntity<List<ChatRoomResponseDto>> getChatRoomsByMember(Member member) {
        List<Participant> participantList = participantRepository.findAllByMemberAndIsActiveTrue(member);
        List<ChatRoom> chatRoomList = new ArrayList<>() ;
        for(Participant participant : participantList){
            ChatRoom chatRoom = chatRoomRepository.findById(participant.getChatRoom().getRoomId())
                    .orElseThrow(()->new CustomException(ErrorCode.INVALID_ROOM));
            chatRoomList.add(chatRoom);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(chatRoomList.stream().map(chatRoomService::makeChatRoomResponseDto).collect(Collectors.toList()));
    }


    /* 채팅방에 참여할 participant 조회*/
    public ResponseEntity<ParticipantResponseDto> getParticipantByMemberAndChatRoom(Member member, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_ROOM));
        Participant participant = participantRepository.findByMemberAndChatRoomAndIsActiveTrue(member,chatRoom)
                .orElseThrow(()->new CustomException(ErrorCode.NOT_A_PARTICIPANT));
        return ResponseEntity.status(HttpStatus.OK)
                .body(ParticipantResponseDto.of(participant));
    }


    /* 참여 중인 채팅방에서 탈퇴 */
    public ResponseEntity<Void> removeParticipantFromChatRoom(Member member, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_ROOM));
        Participant participant = participantRepository.findByMemberAndChatRoomAndIsActiveTrue(member,chatRoom)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PARTICIPANT));
        if(participant.getIsOwner().equals(Boolean.TRUE)) throw new CustomException(ErrorCode.CANNOT_REMOVE_OWNER);
        removeParticipantFromChatRoom(participant);
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }

    /* 채팅방 탈퇴 */
    public void removeParticipantFromChatRoom(Participant participant){
        participant.deactivateParticipant();
    }

    /* 채팅방 환경 설정 수정 */
    /*
    public ResponseEntity<ChatRoomSettingResponseDto> updateSettings(Member member, Long chatRoomId, ChatRoomSettingRequestDto requestDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_ROOM));
        Participant participant = participantRepository.findByMemberAndChatRoomAndIsActiveTrue(member,chatRoom)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PARTICIPANT));
        participant.updateSettings(requestDto.getPositiveKeywords(), requestDto.getNegativeKeywords());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ChatRoomSettingResponseDto.of(participant));
    }

     */

    /* 채팅방 방장 변경 */
    public ResponseEntity<ParticipantResponseDto> updateOwner(Member member, Long chatRoomId, OwnerRequestDto requestDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_ROOM));

        Participant owner = participantRepository.findByMemberAndChatRoomAndIsActiveTrue(member,chatRoom)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PARTICIPANT));
        if(owner.getIsOwner().equals(Boolean.FALSE)) throw new CustomException(ErrorCode.INVALID_OWNER);

        Participant newOwner = participantRepository.findById(requestDto.getNewOwnerId())
                .orElseThrow(()-> new CustomException(ErrorCode.INVALID_PARTICIPANT));
        if(!newOwner.getChatRoom().equals(chatRoom)) throw new CustomException(ErrorCode.NOT_A_PARTICIPANT);
        if(newOwner.getIsActive().equals(Boolean.FALSE)) throw new CustomException(ErrorCode.INVALID_PARTICIPANT);

        owner.updateIsOwner(Boolean.FALSE);
        newOwner.updateIsOwner(Boolean.TRUE);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ParticipantResponseDto.of(newOwner));
    }


    /* 방장 권한 회원 탈퇴 */
    public ResponseEntity<Void> removeParticipantByOwner(Member member, Long chatRoomId, Long participantId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_ROOM));

        Participant owner = participantRepository.findByMemberAndChatRoomAndIsActiveTrue(member,chatRoom)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PARTICIPANT));
        if(owner.getIsOwner().equals(Boolean.FALSE)) throw new CustomException(ErrorCode.INVALID_OWNER);

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(()-> new CustomException(ErrorCode.INVALID_PARTICIPANT));
        if(!participant.getChatRoom().equals(chatRoom)) throw new CustomException(ErrorCode.NOT_A_PARTICIPANT);
        if(participant.getIsOwner().equals(Boolean.TRUE)) throw new CustomException(ErrorCode.CANNOT_REMOVE_OWNER);

        removeParticipantFromChatRoom(participant);
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }
}
