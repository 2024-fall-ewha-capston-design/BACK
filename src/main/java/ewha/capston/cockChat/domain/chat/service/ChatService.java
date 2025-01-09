package ewha.capston.cockChat.domain.chat.service;

import ewha.capston.cockChat.domain.chat.domain.Chat;
import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import ewha.capston.cockChat.domain.chat.domain.Participant;
import ewha.capston.cockChat.domain.chat.dto.ChatRoomRequestDto;
import ewha.capston.cockChat.domain.chat.dto.ChatRoomResponseDto;
import ewha.capston.cockChat.domain.chat.mongo.MongoChatRepository;
import ewha.capston.cockChat.domain.chat.repository.ChatRoomRepository;
import ewha.capston.cockChat.domain.chat.repository.ParticipantRepository;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.member.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    @Autowired
    private MongoChatRepository mongoChatRepository;

    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;

    /* mongoDB 연결 확인 */
    public void mongoDBConnectionTest(){
        Chat chat = new Chat(1L, 1L, "test", Boolean.FALSE);
        mongoChatRepository.save(chat);
    }

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
                .roomType(requestDto.getRoomType())
                .nicknameType(requestDto.getNicknameType())
                .password(requestDto.getPassword())
                .identifier(identifier)
                .build());

        /* 채팅방 생성인을 participant 로 채팅방에 참가.*/
        String roomNickname = "별명";
        if(chatRoom.getNicknameType() == Boolean.FALSE) roomNickname = "별명"; // 별명 사용 채팅방인 경우
        else roomNickname = member.getNickname(); // 실명 사용 채팅방인 경우
        Participant participant = participantRepository.save(Participant.builder()
                        .chatRoom(chatRoom)
                        .member(member)
                        .role(Boolean.FALSE)
                        .roomNickname(roomNickname)
                        .build()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ChatRoomResponseDto.of(chatRoom));
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
}
