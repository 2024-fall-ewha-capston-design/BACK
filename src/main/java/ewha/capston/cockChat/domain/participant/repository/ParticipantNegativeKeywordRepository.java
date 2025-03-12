package ewha.capston.cockChat.domain.participant.repository;

import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.participant.domain.ParticipantNegativeKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantNegativeKeywordRepository extends JpaRepository<ParticipantNegativeKeyword, Long> {
    Long countByParticipant(Participant participant);
}
