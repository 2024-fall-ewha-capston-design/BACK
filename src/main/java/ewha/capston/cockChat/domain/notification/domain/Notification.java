package ewha.capston.cockChat.domain.notification.domain;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;

    private String chatId;

    private Long keywordId;

    private Long participantId;

    @Builder
    public Notification(String chatId, Long keywordId, Long participantId){
        this.chatId =chatId;
        this.keywordId = keywordId;
        this.participantId = participantId;
    }
}
