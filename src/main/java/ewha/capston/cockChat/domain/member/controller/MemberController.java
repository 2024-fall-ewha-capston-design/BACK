package ewha.capston.cockChat.domain.member.controller;

import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.member.dto.request.MemberUpdateRequestDto;
import ewha.capston.cockChat.domain.member.dto.response.MemberResponseDto;
import ewha.capston.cockChat.domain.member.dto.response.ReissueResponseDto;
import ewha.capston.cockChat.domain.member.service.AuthService;
import ewha.capston.cockChat.domain.member.service.MemberService;
import ewha.capston.cockChat.domain.member.dto.request.LoginRequestDto;
import ewha.capston.cockChat.domain.member.dto.response.LoginResponseDto;
import ewha.capston.cockChat.domain.participant.dto.ParticipantResponseDto;
import ewha.capston.cockChat.domain.participant.service.ParticipantService;
import ewha.capston.cockChat.global.config.auth.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ParticipantService participantService;
    private final AuthService authService;

    /* 로그인 */
    @PostMapping("/auth/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto) throws IOException {
        LoginResponseDto responseDto = authService.googleLogin(requestDto.getCode());
        return responseDto;
    }

    /* 토큰 재발급 */
    @PostMapping("/auth/reissue")
    public ReissueResponseDto tokenReissue(HttpServletRequest httpServletRequest){
        return authService.reissue(httpServletRequest);
    }

    /* 프로필 업데이트 */
    @PutMapping("/members/profile")
    public ResponseEntity<MemberResponseDto> updateMember(@AuthUser Member member, @RequestBody MemberUpdateRequestDto requestDto){
        return memberService.updateMember(member,requestDto);
    }

    /* 프로필 조회 */
    @GetMapping("/members/my")
    public ResponseEntity<MemberResponseDto> getMemberProfile(@AuthUser Member member){
        return memberService.getMemberProfile(member);
    }

    /* 익명 프로필 목록 조회 */
    @GetMapping("/members/my/anonymous-profile")
    public ResponseEntity<List<ParticipantResponseDto>> getAnonymousProfileListByMember(@AuthUser Member member){
        return participantService.getAnonymousProfileListByMember(member);
    }
}
