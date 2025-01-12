package ewha.capston.cockChat.domain.chat.dto;

import ewha.capston.cockChat.domain.chat.domain.MessageType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatResponseDto {
    private Long roomId;
    private Long senderId;
    private MessageType type;
    private String content;
}
