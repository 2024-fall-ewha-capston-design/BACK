package ewha.capston.cockChat.domain.chat.domain;

import ewha.capston.cockChat.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor
@Document(collection = "chats")
public class Chat {
    @Id
    private String id;

    @Field("chatroom_id")
    private Long chatroomId;

    @Field("participant_id")
    private Long participantId;

    private String message;

    private MessageType messageType;

    @Builder
    public Chat(Long chatroomId, Long participantId, String message, MessageType chatType){
        this.chatroomId = chatroomId;
        this.messageType = chatType;
        this.message = message;
        this.participantId = participantId;
    }

}
