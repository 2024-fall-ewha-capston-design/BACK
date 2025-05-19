package ewha.capston.cockChat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@EnableMongoAuditing
@SpringBootApplication
public class CockChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(CockChatApplication.class, args);
	}

}
