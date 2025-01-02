package ewha.capston.cockChat.domain.member.dto.response;

import ewha.capston.cockChat.domain.member.domain.Member;
import lombok.Builder;
import lombok.Getter;


@Getter
public class LoginResponseDto {
    private Boolean isExist;
    private String email;
    private String nickname;
    private String accessToken;

    @Builder
    public LoginResponseDto(Boolean isExist, Member member, String accessToken){
        this.isExist = isExist;
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.accessToken = accessToken;
    }
}
