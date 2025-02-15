package ewha.capston.cockChat.domain.member.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberUpdateRequestDto {
    private String nickname;
    private String profileImgUrl;
}
