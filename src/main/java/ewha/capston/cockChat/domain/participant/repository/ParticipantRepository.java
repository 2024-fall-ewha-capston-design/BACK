package ewha.capston.cockChat.domain.participant.repository;

import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.participant.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Boolean existsByChatRoomAndMember(ChatRoom chatRoom, Member member);
}
