package ewha.capston.cockChat.domain.participant.dto.response;

import ewha.capston.cockChat.domain.participant.domain.ParticipantPositiveKeyword;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PositiveKeywordResponseDto {
    private Long keywordId;
    private String content;

    public static PositiveKeywordResponseDto of(ParticipantPositiveKeyword positiveKeyword){
        return PositiveKeywordResponseDto.builder()
                .keywordId(positiveKeyword.getPositiveKeywordId())
                .content(positiveKeyword.getContent())
                .build();
    }
}
