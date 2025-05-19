package ewha.capston.cockChat.domain.participant.dto.response;

import ewha.capston.cockChat.domain.participant.domain.ParticipantNegativeKeyword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;

@Getter
@Builder
@AllArgsConstructor
public class NegativeKeywordResponseDto {
    private Long keywordId;
    private String content;
    private Long penaltyScore;

    public static NegativeKeywordResponseDto of(ParticipantNegativeKeyword negativeKeyword){
        return NegativeKeywordResponseDto.builder()
                .keywordId(negativeKeyword.getNegativeKeywordId())
                .content(negativeKeyword.getContent())
                .penaltyScore(negativeKeyword.getPenaltyScore())
                .build();
    }
}
