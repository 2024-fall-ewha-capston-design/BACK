package ewha.capston.cockChat.domain.member.auth;

import lombok.Data;

@Data
public class GoogleOauthToken {
    private String access_token;
    private int expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}
