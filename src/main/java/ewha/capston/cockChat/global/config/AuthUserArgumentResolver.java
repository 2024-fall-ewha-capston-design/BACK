package ewha.capston.cockChat.global.config;

import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.member.service.JwtTokenProvider;
import ewha.capston.cockChat.global.exception.CustomException;
import ewha.capston.cockChat.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtTokenProvider tokenProvider;


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        AuthUser authUserAnnotation = parameter.getParameterAnnotation(AuthUser.class);
        assert authUserAnnotation != null;
        if(!authUserAnnotation.required()){
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String token = tokenProvider.resolveToken(Objects.requireNonNull(webRequest.getNativeRequest(HttpServletRequest.class)));

        Authentication authentication = tokenProvider.getAuthentication(token);
        return (Member) authentication.getPrincipal();
    }
}
