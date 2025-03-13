package ewha.capston.cockChat.domain.participant.domain;

import ewha.capston.cockChat.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipantPositiveKeyword {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long positiveKeywordId;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @Column(nullable = false)
    private String content;

    @Builder
    public ParticipantPositiveKeyword(Participant participant, String content){
        this.participant = participant;
        this.content = content;
    }
}
