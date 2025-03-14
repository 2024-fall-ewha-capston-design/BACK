package ewha.capston.cockChat.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSecurity
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected boolean sameOriginDisabled() {
        return true; // WebSocket 요청이 Security에 의해 차단되지 않도록 설정
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpDestMatchers("/ws-chat/**").permitAll() // WebSocket 엔드포인트는 인증 없이 접근 가능
                //.anyMessage().authenticated()
        ; // 그 외의 모든 메시지는 인증 필요
    }
}

