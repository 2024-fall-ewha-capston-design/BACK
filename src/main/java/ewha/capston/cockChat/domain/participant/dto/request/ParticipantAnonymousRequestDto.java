package ewha.capston.cockChat.domain.participant.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParticipantAnonymousRequestDto {
    private Boolean isOwner;
    private String roomNickname;
    private String participantImgUrl;
}
