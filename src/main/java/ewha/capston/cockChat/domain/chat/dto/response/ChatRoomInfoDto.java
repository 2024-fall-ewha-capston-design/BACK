package ewha.capston.cockChat.domain.chat.dto.response;

import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import ewha.capston.cockChat.domain.participant.dto.ParticipantResponseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomInfoDto {
    private String roomName;
    private String identifier;
    private List<ParticipantResponseDto> participantList;

    public static ChatRoomInfoDto of(ChatRoom chatRoom, List<ParticipantResponseDto> responseDtoList){
        return ChatRoomInfoDto.builder()
                .roomName(chatRoom.getRoomName())
                .identifier(chatRoom.getIdentifier())
                .participantList(responseDtoList)
                .build();
    }


}
