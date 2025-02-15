package ewha.capston.cockChat.domain.member.dto.response;

import ewha.capston.cockChat.domain.member.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberResponseDto {
    private Long memberId;
    private String email;
    private String nickname;
    private String profileImgUrl;

    public static MemberResponseDto of(Member member){
        return MemberResponseDto.builder()
                .memberId(1L) // 이후 수정
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImgUrl(member.getProfileImgUrl())
                .build();
    }
}
