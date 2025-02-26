package ewha.capston.cockChat.domain.chat.dto;

import ewha.capston.cockChat.domain.chat.domain.Chat;
import ewha.capston.cockChat.domain.chat.domain.MessageType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatResponseDto {
    private Long roomId;
    private Long senderId;
    private MessageType type;
    private String content;
    private LocalDateTime createdAt;

    public static ChatResponseDto of(Chat chat){
        return ChatResponseDto.builder()
                .roomId(chat.getChatroomId())
                .senderId(chat.getParticipantId())
                .type(chat.getMessageType())
                .content(chat.getMessage())
                .createdAt(chat.getCreatedDate())
                .build();
    }
}
