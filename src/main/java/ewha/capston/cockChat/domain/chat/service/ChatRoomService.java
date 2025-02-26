package ewha.capston.cockChat.domain.chat.service;

import ewha.capston.cockChat.domain.chat.domain.Chat;
import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import ewha.capston.cockChat.domain.chat.dto.ChatRoomRequestDto;
import ewha.capston.cockChat.domain.chat.dto.ChatRoomResponseDto;
import ewha.capston.cockChat.domain.chat.mongo.MongoChatRepository;
import ewha.capston.cockChat.domain.chat.repository.ChatRoomRepository;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.participant.repository.ParticipantRepository;
import ewha.capston.cockChat.global.exception.CustomException;
import ewha.capston.cockChat.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MongoChatRepository mongoChatRepository;
    private final ParticipantRepository participantRepository;


    /* 체팅방 생성 */
    public ResponseEntity<ChatRoomResponseDto> createChatRoom(ChatRoomRequestDto requestDto, Member member) {

        /* 6자리의 랜덤 문자열 생성 : 중복 없도록 생성 */
        String identifier = null;
        while(Boolean.TRUE){
            identifier = generateRandomMixStr(6,true);
            if(chatRoomRepository.existsByIdentifier(identifier) == Boolean.FALSE) break;
        }

        /* 채팅방 생성 */
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .roomName(requestDto.getRoomName())
                .isSecretChatRoom(requestDto.getIsSecretChatRoom())
                .isAnonymousChatRoom(requestDto.getIsAnonymousChatRoom())
                .password(requestDto.getPassword())
                .identifier(identifier)
                .chatRoomImgUrl(requestDto.getChatRoomImgUrl())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(makeChatRoomResponseDto(chatRoom));
    }


    /* 채팅방 고유 코드로 채팅방 조회 */
    public ResponseEntity<ChatRoomResponseDto> getChatRoomByIdentifier(Member member, String code) {
        ChatRoom chatRoom = chatRoomRepository.findByIdentifier(code)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_ROOM));
        return ResponseEntity.status(HttpStatus.OK)
                .body(makeChatRoomResponseDto(chatRoom));
    }

    /* 랜덤 문자열 생성 */
    public  String generateRandomMixStr(int length, boolean isUpperCase) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return isUpperCase ? sb.toString() : sb.toString().toLowerCase();
    }

    /* 채팅방 이름으로 채팅방 조회 */
    public ResponseEntity<List<ChatRoomResponseDto>> getChatRoomListByRoomName(String roomName) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findByRoomNameContaining(roomName);
        List<ChatRoomResponseDto> responseDtoList = new ArrayList<>();
        for(ChatRoom chatRoom : chatRoomList){
            if(chatRoom.getIsSecretChatRoom().equals(Boolean.FALSE)){
                responseDtoList.add(makeChatRoomResponseDto(chatRoom));
            }
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDtoList);
    }

    /* 채팅방 삭제 */
    public ResponseEntity<Void> removeChatRoom(Member member, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_ROOM));
        Participant owner = participantRepository.findByMemberAndChatRoom(member,chatRoom)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PARTICIPANT));
        if(owner.getIsOwner().equals(Boolean.FALSE)) throw new CustomException(ErrorCode.INVALID_OWNER);

        List<Participant> participantList = participantRepository.findAllByChatRoom(chatRoom);
        for(Participant participant : participantList){
            participantRepository.delete(participant);
        }

        chatRoomRepository.delete(chatRoom);
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }

    /* chatRoomResponseDto 생성 */
    public ChatRoomResponseDto makeChatRoomResponseDto(ChatRoom chatRoom){
        return ChatRoomResponseDto.of(chatRoom,participantRepository.countByChatRoom(chatRoom),mongoChatRepository.findTopByChatroomIdOrderByCreatedDateDesc(chatRoom.getRoomId()));
    }
}
