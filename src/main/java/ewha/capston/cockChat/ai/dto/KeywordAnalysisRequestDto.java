package ewha.capston.cockChat.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KeywordAnalysisRequestDto {
    @JsonProperty("keyword_id")
    private String keywordId;

    private String content;
}
