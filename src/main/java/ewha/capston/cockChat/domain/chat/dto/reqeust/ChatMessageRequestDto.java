package ewha.capston.cockChat.domain.chat.dto.reqeust;

import ewha.capston.cockChat.domain.chat.domain.MessageType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageRequestDto {
    private Long roomId;
    private MessageType type;
    private Long senderId;
    private String content;
}
