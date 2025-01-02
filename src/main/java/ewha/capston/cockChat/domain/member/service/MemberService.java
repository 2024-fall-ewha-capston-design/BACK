package ewha.capston.cockChat.domain.member.service;


import ewha.capston.cockChat.domain.member.auth.GoogleUser;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.member.repository.MemberRepository;
import ewha.capston.cockChat.global.exception.CustomException;
import ewha.capston.cockChat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    /* 멤버 생성 */
    public Member saveMember(@RequestBody GoogleUser googleUser) {
        Member member = Member.builder()
                .email(googleUser.getEmail())
                .nickname(googleUser.getEmail())
                .build();
        memberRepository.save(member);
        return member;
    }

    /* 신규 회원인지 조사 */
    @Transactional(readOnly = true)
    public Boolean checkJoined(String email) {
        System.out.println("checkJoined emailL "+email);
        Boolean isJoined = memberRepository.existsMemberByEmail(email);
        return isJoined;
    }

    /* 이메일로 멤버 조회 */
    @Transactional(readOnly = true)
    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(ErrorCode.NO_MEMBER_EXIST));
    }

    /* 닉네임 중복 조회 */
    @Transactional(readOnly = true)
    public Boolean isNicknameExist(Member member, String nickname) {
        /* 본인 닉네임은 중복 조회에서 제외시킴. */
        if(member.getNickname().equals(nickname)){
            return false;
        }
        return memberRepository.existsMemberByNickname(nickname);
    }

    /* 닉네임으로 멤버 조회 */
    @Transactional(readOnly = true)
    public Member findMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname)
                .orElseThrow(()->new CustomException(ErrorCode.NO_MEMBER_EXIST));
    }


}
