package ewha.capston.cockChat.domain.notification.repository;

import ewha.capston.cockChat.domain.notification.domain.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoNotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByParticipantIdInOrderByCreatedDateDesc(List<Long> participantIds);
}
