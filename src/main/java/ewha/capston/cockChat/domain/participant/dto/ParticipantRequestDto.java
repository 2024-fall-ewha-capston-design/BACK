package ewha.capston.cockChat.domain.participant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParticipantRequestDto {
    private String roomNickname;
    private String participantImgUrl;
}
