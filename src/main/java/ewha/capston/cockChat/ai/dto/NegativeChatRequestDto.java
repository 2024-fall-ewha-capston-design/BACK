package ewha.capston.cockChat.ai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NegativeChatRequestDto {
    private String chat_history;
    private String negative_keywords;
}
