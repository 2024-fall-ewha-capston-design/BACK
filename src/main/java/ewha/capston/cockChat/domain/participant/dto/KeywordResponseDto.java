package ewha.capston.cockChat.domain.participant.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class KeywordResponseDto {
    private Long keywordId;
    private String content;

    public static KeywordResponseDto of(Long keywordId, String content){
        return KeywordResponseDto.builder()
                .keywordId(keywordId)
                .content(content)
                .build();
    }
}
