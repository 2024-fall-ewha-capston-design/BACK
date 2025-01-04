package ewha.capston.cockChat.domain.chat.domain;

import ewha.capston.cockChat.global.BaseTimeEntity;
import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "chats")
public class Chat {
    @Id
    private String id;

    @Field("chatroom_id")
    private Long chatroomId;

    @Field("participant_id")
    private Long participantId;

    private String message;

    private Boolean chatType;

    public Chat(Long chatroomId, Long participantId, String message, Boolean chatType){
        this.chatroomId = chatroomId;
        this.chatType = chatType;
        this.message = message;
        this.participantId = participantId;
    }

}
