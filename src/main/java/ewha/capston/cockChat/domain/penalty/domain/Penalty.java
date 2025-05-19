package ewha.capston.cockChat.domain.penalty.domain;

import ewha.capston.cockChat.domain.participant.domain.Participant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Penalty {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long penaltyId;

    /* 벌점을 부여한 기준이 되는 참가자 (즉, 이 참가자가 불쾌함을 느낌) */
    @ManyToOne
            //(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_participant_id", nullable = false)
    private Participant receiverParticipant;

    /* 벌점을 받은 참가자 (즉, 부정 키워드 포함 메시지를 보낸 사람)*/
    @ManyToOne
            //(fetch = FetchType.LAZY)
    @JoinColumn(name = "offender_participant_id", nullable = false)
    private Participant offenderParticipant;

    @Column(nullable = false)
    private Long penaltyCount;

    @Builder
    public Penalty(Participant receiverParticipant, Participant offenderParticipant, Long penaltyCount) {
        this.receiverParticipant = receiverParticipant;
        this.offenderParticipant = offenderParticipant;
        this.penaltyCount = penaltyCount;
    }

    /* 벌점 부여 */
    public void increasePenalty(Long score) {
        this.penaltyCount += score;
    }
}
