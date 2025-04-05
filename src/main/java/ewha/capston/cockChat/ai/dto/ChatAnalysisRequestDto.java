package ewha.capston.cockChat.ai.dto;

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
    private Map<String, List<String>> participantKeywords;
}
