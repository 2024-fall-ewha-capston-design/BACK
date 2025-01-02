package ewha.capston.cockChat.domain.member.controller;

import ewha.capston.cockChat.domain.member.service.AuthService;
import ewha.capston.cockChat.domain.member.service.MemberService;
import ewha.capston.cockChat.domain.member.dto.request.LoginRequestDto;
import ewha.capston.cockChat.domain.member.dto.response.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;

    /* 로그인 */
    @PostMapping("/auth/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto) throws IOException {
        LoginResponseDto responseDto = authService.googleLogin(requestDto.getCode());
        return responseDto;
    }

}
