package ewha.capston.cockChat.domain.penalty.dto;

import ewha.capston.cockChat.domain.penalty.domain.Penalty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PenaltyResponseDto {
    private Long offenderId;

    public static PenaltyResponseDto of(Penalty penalty){
        return PenaltyResponseDto.builder()
                .offenderId(penalty.getOffenderParticipant().getParticipantId())
                .build();
    }
}
