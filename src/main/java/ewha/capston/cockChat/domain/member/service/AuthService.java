package ewha.capston.cockChat.domain.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ewha.capston.cockChat.domain.member.auth.GoogleOauthToken;
import ewha.capston.cockChat.domain.member.auth.GoogleUser;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.member.dto.response.LoginResponseDto;
import ewha.capston.cockChat.domain.member.dto.response.ReissueResponseDto;
import ewha.capston.cockChat.domain.member.dto.response.TokenResponseDto;
import ewha.capston.cockChat.global.exception.CustomException;
import ewha.capston.cockChat.global.exception.ErrorCode;
import ewha.capston.cockChat.global.service.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final RedisService redisService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String GOOGLE_SNS_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String GOOGLE_SNS_CALLBACK_URL;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String GOOGLE_SNS_CLIENT_SECRET;

    @Value("${spring.OAuth2.google.url.token}")
    private String GOOGLE_TOKEN_REQUEST_URL;

    @Value("${spring.OAuth2.google.url.profile}")
    private String GOOGLE_USERINFO_REQUEST_URL;


    /* 구글 로그인 */
    public LoginResponseDto googleLogin(String code) throws IOException {
        GoogleOauthToken oauthToken = getAccessToken(code);

        GoogleUser googleUser = getUserInfo(oauthToken);

        /* 이메일을 통해 이미 회원가입된 유저인지 확인 */
        String email = googleUser.getEmail();
        Boolean isExsingtingMember = memberService.checkJoined(email);

        Member member;

        /* 회원가입되어 있지 않은 경우, 회원가입 */
        if(!isExsingtingMember){
            member = memberService.saveMember(googleUser);
        }
        /* 이미 회원가입되어있는 유저인 경우, 이메일을 통해 회원 조회 */
        else{
            member = memberService.findMemberByEmail(email);
        }

        /* 회원 인가 처리를 위한 토큰 발행 */
        TokenResponseDto tokenDto = jwtTokenProvider.createToken(member.getEmail());
        redisService.setValues(member.getEmail(), tokenDto.getRefreshToken(),
                jwtTokenProvider.getTokenExpirationTime(tokenDto.getAccessToken()));

        return new LoginResponseDto(isExsingtingMember, member, tokenDto.getAccessToken());
    }

    /* 일회용 code 를 구글로 보냄 -> 액세스 토큰을 포함한 JSON String 이 담긴 응답을 받아옴 */
    private GoogleOauthToken getAccessToken(String code) throws JsonProcessingException {
        Map<String,Object> params = new HashMap<>();
        params.put("code",code);
        params.put("client_id" , GOOGLE_SNS_CLIENT_ID);
        params.put("client_secret",GOOGLE_SNS_CLIENT_SECRET);
        params.put("redirect_uri", GOOGLE_SNS_CALLBACK_URL);
        params.put("grant_type","authorization_code");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(GOOGLE_TOKEN_REQUEST_URL,
                params, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        GoogleOauthToken googleOauthToken = objectMapper.readValue(response.getBody() , GoogleOauthToken.class);
        return googleOauthToken;
    }

    private GoogleUser getUserInfo(GoogleOauthToken oauthToken) throws JsonProcessingException{

        /* header 에 accessToken 담기 */
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization","Bearer "+oauthToken.getAccess_token());

        /* HttpEntity 생성 -> 헤더 담음 -> restTemplate 으로 구글과 통신 */
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(GOOGLE_USERINFO_REQUEST_URL , HttpMethod.GET , request , String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        GoogleUser googleUser = objectMapper.readValue(response.getBody() , GoogleUser.class);
        return googleUser;
    }

    /* 토큰 재발급 */
    public ReissueResponseDto reissue(HttpServletRequest request){

        /* accessToken 으로부터 email 획득 */
        String accessToken = jwtTokenProvider.resolveToken(request);
        /* 만료된 accessToken 디코딩 -> Payload 값 획득 */
        HashMap<String , String> payloadMap = getPayloadByToken(accessToken);
        String email = payloadMap.get("sub");

        /* Redis 에서 email 을 기반으로 저장된 Refresh Token 획득 */
        String refreshToken = redisService.getValues(email);

        /* Refresh Token 검증 */
        /* Redis 에 없는 경우 다시 로그인 */
        if(refreshToken ==null) throw new CustomException(ErrorCode.NON_LOGIN);
        /* Token 이 만료된 경우 다시 로그인 */
        if(!jwtTokenProvider.validateToken(refreshToken)) throw new CustomException(ErrorCode.NON_LOGIN);

        /* email 로 멤버 조회 */
        Member member = memberService.findMemberByEmail(email);

        /* 새 토큰 생성 */
        TokenResponseDto newToken = jwtTokenProvider.createToken(email);

        /* Refresh Token 으로 Redis 없데이트 */
        redisService.setValues(email , newToken.getRefreshToken() , jwtTokenProvider.getTokenExpirationTime(newToken.getRefreshToken()));

        return new ReissueResponseDto(newToken.getAccessToken() , member);
    }

    private HashMap<String, String> getPayloadByToken(String accessToken) {
        try {
            String[] splitJwt = accessToken.split("\\.");

            Base64.Decoder decoder = Base64.getDecoder();
            String payload = new String(decoder.decode(splitJwt[1].getBytes()));

            return new ObjectMapper().readValue(payload , HashMap.class);
        } catch (JsonProcessingException e){
            log.error(e.getMessage());
            return null;
        }
    }

}
