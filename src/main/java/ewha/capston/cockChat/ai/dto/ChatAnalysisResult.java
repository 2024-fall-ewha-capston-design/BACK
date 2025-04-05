package ewha.capston.cockChat.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatAnalysisResult {
    @JsonProperty("chat_id")
    private String chatId;

    @JsonProperty("keyword_id")
    private String keywordId;

    @JsonProperty("participant_id")
    private String participantId;
}
