package ewha.capston.cockChat.domain.chat.mongo;

import ewha.capston.cockChat.domain.chat.domain.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
public interface MongoChatRepository extends MongoRepository<Chat, String> {
    Chat findTopByChatroomIdOrderByCreatedDateDesc(Long chatroomId);
    List<Chat> findAllByChatroomIdOrderByCreatedDateAsc(Long chatRoomId);
}

