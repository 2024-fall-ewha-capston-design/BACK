package ewha.capston.cockChat.domain.participant.dto;

import ewha.capston.cockChat.domain.participant.domain.Participant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipantResponseDto {
    private Long participantId;
    private String roomNickname;
    private Long roomId;
    private Boolean isOwner;
    private String participantImgUrl;

    public static ParticipantResponseDto of(Participant participant){
        return ParticipantResponseDto.builder()
                .participantId(participant.getParticipantId())
                .roomNickname(participant.getRoomNickname())
                .roomId(participant.getChatRoom().getRoomId())
                .isOwner(participant.getIsOwner())
                .participantImgUrl(participant.getParticipantImgUrl())
                .build();
    }
}
