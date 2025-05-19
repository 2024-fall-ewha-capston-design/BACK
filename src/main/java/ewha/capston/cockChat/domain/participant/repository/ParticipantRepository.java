package ewha.capston.cockChat.domain.participant.repository;

import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.participant.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Boolean existsByChatRoomAndMember(ChatRoom chatRoom, Member member);
    Boolean existsByChatRoomAndMemberAndIsActiveTrue(ChatRoom chatRoom, Member member);

    Optional<Participant> findByMemberAndChatRoom(Member member, ChatRoom chatRoom);
    Optional<Participant> findByMemberAndChatRoomAndIsActiveTrue(Member member, ChatRoom chatRoom);

    List<Participant> findAllByMember(Member member);
    List<Participant> findAllByMemberAndIsActiveTrue(Member member);

    List<Participant> findAllByChatRoom(ChatRoom chatRoom);
    List<Participant> findAllByIsActiveTrue();

    List<Participant> findAllByChatRoomAndIsActiveTrue(ChatRoom chatRoom);

    Long countByChatRoom(ChatRoom chatRoom);
}
