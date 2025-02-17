package ewha.capston.cockChat.domain.chat.dto;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomRequestDto {
    private String roomName;
    private Boolean roomType;
    private Long password;
    private Boolean nicknameType;
    private String chatRoomImgUrl;
}
