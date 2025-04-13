package ewha.capston.cockChat.domain.notification.domain;

import ewha.capston.cockChat.global.BaseTimeEntity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@Document(collection = "notifications")
public class Notification extends BaseTimeEntity {
    @Id
    private String id;

    private String chatId;

    private Long keywordId;

    private Long participantId;

    private boolean isRead;


    @Builder
    public Notification(String chatId, Long keywordId, Long participantId, boolean isRead){
        this.chatId =chatId;
        this.keywordId = keywordId;
        this.participantId = participantId;
        this.isRead = isRead;
    }
}
