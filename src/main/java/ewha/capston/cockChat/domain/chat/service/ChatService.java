package ewha.capston.cockChat.domain.chat.service;

import ewha.capston.cockChat.domain.chat.domain.Chat;
import ewha.capston.cockChat.domain.chat.mongo.MongoChatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    @Autowired
    private MongoChatRepository mongoChatRepository;

    /* mongoDB 연결 확인 */
    public void mongoDBConnectionTest(){
        Chat chat = new Chat(1L, 1L, "test", Boolean.FALSE);
        mongoChatRepository.save(chat);
    }

}
