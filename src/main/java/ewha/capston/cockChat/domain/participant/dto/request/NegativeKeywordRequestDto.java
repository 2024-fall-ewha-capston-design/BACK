package ewha.capston.cockChat.domain.participant.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NegativeKeywordRequestDto {
    private String keyword;
    private Long penaltyScore;
}
