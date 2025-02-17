package ewha.capston.cockChat.domain.chat.dto;

import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomResponseDto {

    private Long roomId;
    private String identifier;
    private Long lastChat;
    private String roomName;
    private Boolean roomType;
    private Long password;
    private Boolean nicknameType;
    private String chatRoomImgUrl;

    public static ChatRoomResponseDto of(ChatRoom chatRoom){
        return ChatRoomResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .identifier(chatRoom.getIdentifier())
                .lastChat(0L) // 이후 수정
                .roomName(chatRoom.getRoomName())
                .roomType(chatRoom.getRoomType())
                .password(chatRoom.getPassword())
                .nicknameType(chatRoom.getNicknameType())
                .chatRoomImgUrl(chatRoom.getChatRoomImgUrl())
                .build();
    }
}
