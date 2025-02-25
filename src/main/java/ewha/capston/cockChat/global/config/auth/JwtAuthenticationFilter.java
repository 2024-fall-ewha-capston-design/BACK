package ewha.capston.cockChat.global.config.auth;

import ewha.capston.cockChat.domain.member.service.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /* 헤더에서 JWT 를 받아옵니다. */
        String token = jwtTokenProvider.resolveToken(request);

        /* 유효한 토큰인지 확인합니다. 유효성검사 */
        if (token != null && jwtTokenProvider.validateToken(token)) {
            /* 토큰 인증과정을 거친 결과를 authentication 이라는 이름으로 저장해줌. */
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            /* SecurityContext 에 Authentication 객체를 저장합니다. */
            /* token 이 인증된 상태를 유지하도록 context(맥락)을 유지해줌 */
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        /* UsernamePasswordAuthenticationFilter 로 이동 */
        filterChain.doFilter(request, response);
    }
}
