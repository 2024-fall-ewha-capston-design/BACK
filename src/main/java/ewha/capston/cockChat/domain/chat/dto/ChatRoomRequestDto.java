package ewha.capston.cockChat.domain.chat.dto;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomRequestDto {
    private String roomName;
    private Boolean isSecretChatRoom;
    private Long password;
    private Boolean isAnonymousChatRoom;
    private String chatRoomImgUrl;
}
