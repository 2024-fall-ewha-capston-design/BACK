package ewha.capston.cockChat.domain.participant.repository;


import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.participant.domain.ParticipantPositiveKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantPositiveKeywordRepository extends JpaRepository<ParticipantPositiveKeyword, Long> {
    Long countByParticipant(Participant participant);

    List<ParticipantPositiveKeyword> findAllByParticipant(Participant participant);
}
