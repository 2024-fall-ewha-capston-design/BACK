package ewha.capston.cockChat.domain.notification.repository;

import ewha.capston.cockChat.domain.notification.domain.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoNotificationRepository extends MongoRepository<Notification, String> {
}
