package ewha.capston.cockChat.domain.notification.dto;

import ewha.capston.cockChat.domain.chat.domain.Chat;
import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import ewha.capston.cockChat.domain.notification.domain.Notification;
import ewha.capston.cockChat.domain.participant.domain.ParticipantPositiveKeyword;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationResponseDto {
    private String notificationId;
    private String keyword; // 키워드 내용
    private String chatId; // 채팅 id
    private Long chatRoomId; // 채팅 발생 채팅방 id
    private String chatRoomName;
    private Long participantId; // 알림 소유자 participant Id
    private boolean isRead; // 채팅 확인 여부

    public static NotificationResponseDto of(Notification notification, Chat chat, ChatRoom chatRoom, ParticipantPositiveKeyword positiveKeyword, boolean isRead){
        return NotificationResponseDto.builder()
                .notificationId(notification.getId())
                .keyword(positiveKeyword.getContent())
                .chatId(chat.getId())
                .chatRoomId(chat.getChatroomId())
                .chatRoomName(chatRoom.getRoomName())
                .participantId(positiveKeyword.getParticipant().getParticipantId())
                .isRead(isRead)
                .build();
    }
}
