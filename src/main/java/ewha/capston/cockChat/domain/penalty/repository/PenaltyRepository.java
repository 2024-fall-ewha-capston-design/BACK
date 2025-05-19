package ewha.capston.cockChat.domain.penalty.repository;

import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.penalty.domain.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
    Optional<Penalty> findByReceiverParticipantAndOffenderParticipant(Participant receiver, Participant offender);
}
