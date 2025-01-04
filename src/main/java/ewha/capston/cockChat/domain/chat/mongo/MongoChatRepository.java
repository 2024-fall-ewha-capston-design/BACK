package ewha.capston.cockChat.domain.chat.mongo;

import ewha.capston.cockChat.domain.chat.domain.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface MongoChatRepository extends MongoRepository<Chat, String> {
}
