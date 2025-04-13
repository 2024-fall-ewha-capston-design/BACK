package ewha.capston.cockChat.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatAnalysisRequestDto {
    private List<String> messages;

    @JsonProperty("participant_keywords")
    private Map<String, List<KeywordAnalysisRequestDto>> participantKeywords;
}
