package ewha.capston.cockChat.domain.chat.repository;

import ewha.capston.cockChat.domain.chat.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
