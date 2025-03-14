package ewha.capston.cockChat.global.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Configuration
//@EnableWebSocket
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /*
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 브로커에 사용할 TaskScheduler 추가 (하트비트 설정을 위해 필수)
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.initialize();

        registry.enableSimpleBroker("/topic")
                .setHeartbeatValue(new long[]{10000, 10000})  // 10초 하트비트
                .setTaskScheduler(taskScheduler);  // 하트비트 스케줄링
        registry.setApplicationDestinationPrefixes("/app");
    }

     */

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue"); // 클라이언트가 구독할 수 있는 prefix
        registry.setApplicationDestinationPrefixes("/app"); // 클라이언트가 서버로 보낼 때 사용하는 prefix
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .setAllowedOrigins("http://localhost:63342","https://chatcipe.o-r.kr", "https://chatcipe.vercel.app")
                .addInterceptors(new HttpSessionHandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(
                            ServerHttpRequest request, ServerHttpResponse response,
                            org.springframework.web.socket.WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        System.out.println("🔍 WebSocket Request Headers: " + request.getHeaders());
                        System.out.println("🔍 Upgrade Header: " + request.getHeaders().getFirst("Upgrade"));
                        System.out.println("🔍 Connection Header: " + request.getHeaders().getFirst("Connection"));
                        return true;
                    }

                    @Override
                    public void afterHandshake(
                            ServerHttpRequest request, ServerHttpResponse response,
                            org.springframework.web.socket.WebSocketHandler wsHandler, Exception exception) {
                        System.out.println("✅ WebSocket Handshake 완료!");
                    }
                });
                //.setAllowedOriginPatterns("*")
                //.setAllowedOrigins("http://localhost:63342","https://chatcipe.o-r.kr", "https://chatcipe.vercel.app", "https://chatcipe.netlify.app")
               //.withSockJS()

    }



    /*
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOrigins("https://chatcipe.o-r.kr", "https://chatcipe.vercel.app", "https://chatcipe.netlify.app") // Vercel 도메인 추가
                .withSockJS()
                .setSessionCookieNeeded(false) // 쿠키 세션 필요 없음
                .setHeartbeatTime(25000) // 25초마다 하트비트 전송
                .setWebSocketEnabled(false); // WebSocket 비활성화, XHR 폴링 강제
    }
    */



/*
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .setAllowedOrigins("https://chatcipe.o-r.kr")

               .setAllowedOriginPatterns("http://localhost:3000", "http://localhost:8080",
                                        "http://localhost:63342", "https://chatcipe.o-r.kr"
                                          ,"wss://chatcipe.o-r.kr"
                                        )


               .withSockJS()
            ;
    }

 */

}

