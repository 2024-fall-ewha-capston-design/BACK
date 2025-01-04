package ewha.capston.cockChat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CockChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(CockChatApplication.class, args);
	}

}
