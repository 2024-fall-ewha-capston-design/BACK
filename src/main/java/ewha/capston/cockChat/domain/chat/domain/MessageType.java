package ewha.capston.cockChat.domain.chat.domain;

public enum MessageType {
    CHAT, // 일반 채팅 메시지
    IMG, // 이미지 메시지
    JOIN, // 채팅방 입장 알림
    LEAVE // 채팅방 퇴장 알림
}
