package ewha.capston.cockChat.domain.chat.dto.reqeust;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomSettingRequestDto {
    private String positiveKeywords;
    private String negativeKeywords;
}
