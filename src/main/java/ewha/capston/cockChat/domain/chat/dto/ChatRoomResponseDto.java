package ewha.capston.cockChat.domain.chat.dto;

import ewha.capston.cockChat.domain.chat.domain.Chat;
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
    private String roomName;
    private Boolean isSecretChatRoom;
    private Long password;
    private Boolean isAnonymousChatRoom;
    private String chatRoomImgUrl;
    private Long participantCount;
    private ChatResponseDto latestChat;

    public static ChatRoomResponseDto of(ChatRoom chatRoom, Long participantCount, Chat chat){
        return ChatRoomResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .identifier(chatRoom.getIdentifier())
                .roomName(chatRoom.getRoomName())
                .isSecretChatRoom(chatRoom.getIsSecretChatRoom())
                .password(chatRoom.getPassword())
                .isAnonymousChatRoom(chatRoom.getIsAnonymousChatRoom())
                .chatRoomImgUrl(chatRoom.getChatRoomImgUrl())
                .participantCount(participantCount)
                .latestChat(chat != null ? ChatResponseDto.of(chat) : null)
                .build();
    }
}
