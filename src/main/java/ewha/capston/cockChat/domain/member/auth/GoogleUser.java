package ewha.capston.cockChat.domain.member.auth;

import lombok.Data;

@Data
public class GoogleUser {
    public String sub;
    public String email;
    public Boolean email_verified;
    public String name;
    public String family_name;
    public String given_name;
    public String picture;
    public String locale;
    public String hd;
}
