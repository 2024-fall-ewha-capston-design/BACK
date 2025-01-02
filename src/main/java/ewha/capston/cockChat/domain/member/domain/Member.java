package ewha.capston.cockChat.domain.member.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String profileImgUrl;

    @Builder
    public Member(String email, String nickname){
        this.email = email;
        this.nickname = nickname;
    }
}
