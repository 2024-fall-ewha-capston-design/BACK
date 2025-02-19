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
    private Boolean isSecretChatRoom;
    private Long password;
    private Boolean isAnonymousChatRoom;
    private String chatRoomImgUrl;

    public static ChatRoomResponseDto of(ChatRoom chatRoom){
        return ChatRoomResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .identifier(chatRoom.getIdentifier())
                .lastChat(0L) // 이후 수정
                .roomName(chatRoom.getRoomName())
                .isSecretChatRoom(chatRoom.getIsSecretChatRoom())
                .password(chatRoom.getPassword())
                .isAnonymousChatRoom(chatRoom.getIsAnonymousChatRoom())
                .chatRoomImgUrl(chatRoom.getChatRoomImgUrl())
                .build();
    }
}
