package ewha.capston.cockChat.domain.participant.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositiveKeywordRequestDto {
    private String keyword;
}
