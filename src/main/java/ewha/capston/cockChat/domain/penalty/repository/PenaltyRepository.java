package ewha.capston.cockChat.domain.penalty.repository;

import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.penalty.domain.Penalty;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
    Optional<Penalty> findByReceiverParticipantAndOffenderParticipant(Participant receiver, Participant offender);

    /* receiverId 기준 벌점 상위 최대 3명 조회 */
    /*
    @Query("SELECT p FROM Penalty p " +
            "WHERE p.receiverParticipant.id = :receiverId " +
            "AND p.offenderParticipant.id <> :receiverId " +
            "ORDER BY p.penaltyCount DESC")
    List<Penalty> findTop3ByReceiverIdOrderByPenaltyCountDescExcludeSelf(
            @Param("receiverId") Long receiverId,
            Pageable pageable
    );

     */
    List<Penalty> findTop3ByReceiverParticipant_ParticipantIdAndOffenderParticipant_ParticipantIdNotOrderByPenaltyCountDesc(
            Long receiverId, Long excludeOffenderId
    );


}
