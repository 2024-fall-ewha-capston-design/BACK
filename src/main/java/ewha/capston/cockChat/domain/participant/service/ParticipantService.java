package ewha.capston.cockChat.domain.participant.service;

import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import ewha.capston.cockChat.domain.chat.repository.ChatRoomRepository;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.participant.dto.ParticipantAnonymousRequestDto;
import ewha.capston.cockChat.domain.participant.dto.ParticipantRealRequestDto;
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
public class ParticipantService {

    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;

    /* 실명 채팅방 신규 입장 */
    public ResponseEntity<ParticipantResponseDto> joinRealNameChatroom(Member member, Long chatRoomId, ParticipantRealRequestDto requestDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_ROOM));

        if(participantRepository.existsByChatRoomAndMember(chatRoom,member).equals(Boolean.TRUE))
            throw new CustomException(ErrorCode.ALREADY_JOINED_MEMBER);

        Participant participant = participantRepository.save(
                Participant.builder()
                        .isOwner(requestDto.getIsOwner())
                        .roomNickname(member.getNickname())
                        .participantImgUrl(member.getProfileImgUrl())
                        .chatRoom(chatRoom)
                        .member(member)
                        .build()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ParticipantResponseDto.of(participant));

    }

    /* 익명 채팅방 신규 입장 */
    public ResponseEntity<ParticipantResponseDto> joinAnonymousChatroom(Member member, Long chatRoomId, ParticipantAnonymousRequestDto requestDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_ROOM));
        if(participantRepository.existsByChatRoomAndMember(chatRoom,member).equals(Boolean.TRUE))
            throw new CustomException(ErrorCode.ALREADY_JOINED_MEMBER);

        Participant participant = participantRepository.save(
                Participant.builder()
                        .isOwner(requestDto.getIsOwner())
                        .roomNickname(requestDto.getRoomNickname())
                        .participantImgUrl(requestDto.getParticipantImgUrl())
                        .chatRoom(chatRoom)
                        .member(member)
                        .build()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ParticipantResponseDto.of(participant));
    }

    /* 회원의 익명 프로필 목록 조회 */
    public ResponseEntity<List<ParticipantResponseDto>> getAnonymousProfileListByMember(Member member) {
        List<Participant> participantList = participantRepository.findAllByMember(member);
        List<ParticipantResponseDto> responseDtoList = new ArrayList<>();
        for(Participant participant : participantList){
            if(participant.getChatRoom().getIsAnonymousChatRoom().equals(Boolean.TRUE)){
                responseDtoList.add(ParticipantResponseDto.of(participant));
            }
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDtoList);
    }

    /* 실명 사용 모든 프로필 업데이트 */
    public void updateAllRealNameProfileList(Member member) {
        List<Participant> participantList = participantRepository.findAllByMember(member);
        participantList.stream()
                .filter(participant -> Boolean.FALSE.equals(participant.getChatRoom().getIsAnonymousChatRoom()))
                .forEach(participant -> participant.updateRealNameParticipant(member));
    }
}
