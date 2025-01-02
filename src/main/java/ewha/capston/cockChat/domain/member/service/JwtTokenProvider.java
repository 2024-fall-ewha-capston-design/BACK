package ewha.capston.cockChat.domain.member.service;

import ewha.capston.cockChat.domain.member.dto.response.TokenResponseDto;
import ewha.capston.cockChat.domain.member.repository.MemberRepository;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.global.exception.CustomException;
import ewha.capston.cockChat.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
public class JwtTokenProvider {
    private final MemberRepository memberRepository;

    @Value("${jwt.token.secret}")
    private String SECRET_KEY;

    @Value("${jwt.token.access-token-validity-in-seconds}")
    private Long ACCESS_TOKEN_VALID_TIME;

    @Value("${jwt.token.refresh-token-validity-in-seconds}")
    private Long REFRESH_TOKEN_VALID_TIME;

    @PostConstruct
    protected void init(){
        SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
    }


    /* JWT 토큰 : 생성 */
    public TokenResponseDto createToken(String email) {
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(email);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        String refreshToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+REFRESH_TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256 , SECRET_KEY)
                .compact();

        return new TokenResponseDto(accessToken,refreshToken);
    }

    /* JWT 토큰 : 인증 정보 조회 */
    public Authentication getAuthentication(String token){
        try{
            String email = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
            Member member = memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.NO_MEMBER_EXIST));
            return new UsernamePasswordAuthenticationToken(member, "");
        }catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.NON_LOGIN);
        }
    }

    /* Request 의 Header 에서 token 획득 */
    public String resolveToken(HttpServletRequest request){
        return request.getHeader("Authorization");
    }

    /* 토큰의 유효성과 만료일자 확인 -> 토큰의 expire 여부를 boolean 으로 반환 */
    public boolean validateToken(String jwtToken){
        try{
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e){
            log.info(ErrorCode.EXPIRED_TOKEN.getMessage());
        } catch (JwtException e){
            log.info(ErrorCode.INVALID_TOKEN.getMessage());
        } catch (IllegalArgumentException e){
            log.info(ErrorCode.NON_LOGIN.getMessage());
        }
        return false;
    }

    public Long getTokenExpirationTime(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getExpiration().getTime();
    }

    /* 토큰으로부터 닉네임 획득 */
    public String getNicknameFromToken(String accessToken){
        String email = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(accessToken).getBody().getSubject();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(ErrorCode.NO_MEMBER_EXIST));
        String nickname = member.getNickname();
        return nickname;
    }

}
