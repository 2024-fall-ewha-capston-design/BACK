package ewha.capston.cockChat.domain.chat.dto.response;

import ewha.capston.cockChat.domain.chat.domain.Chat;
import ewha.capston.cockChat.domain.chat.domain.MessageType;
import ewha.capston.cockChat.domain.participant.domain.Participant;
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
    private String senderNickname;
    private String senderImgUrl;
    private MessageType type;
    private String content;
    private LocalDateTime createdAt;

    public static ChatResponseDto of(Chat chat, Participant participant){
        return ChatResponseDto.builder()
                .roomId(chat.getChatroomId())
                .senderId(chat.getParticipantId())
                .senderNickname(participant.getRoomNickname())
                .senderImgUrl(participant.getParticipantImgUrl())
                .type(chat.getMessageType())
                .content(chat.getMessage())
                .createdAt(chat.getCreatedDate())
                .build();
    }
}
