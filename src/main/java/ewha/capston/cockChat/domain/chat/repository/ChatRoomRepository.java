package ewha.capston.cockChat.domain.chat.repository;

import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Boolean existsByIdentifier(String identifier);

}

