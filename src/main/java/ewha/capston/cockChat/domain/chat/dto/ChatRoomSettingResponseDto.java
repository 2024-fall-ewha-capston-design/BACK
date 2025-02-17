package ewha.capston.cockChat.domain.chat.dto;

import ewha.capston.cockChat.domain.participant.domain.Participant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomSettingResponseDto {
    private Long participantId;
    private String positiveKeywords;
    private String negativeKeywords;

    public static ChatRoomSettingResponseDto of(Participant participant){
        return ChatRoomSettingResponseDto
                .builder()
                .participantId(participant.getParticipantId())
                .positiveKeywords(participant.getPositiveKeywords())
                .negativeKeywords(participant.getNegativeKeywords())
                .build();
    }
}
