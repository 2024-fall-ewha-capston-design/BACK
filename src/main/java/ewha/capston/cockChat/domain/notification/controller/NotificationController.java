package ewha.capston.cockChat.domain.notification.controller;

import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.notification.dto.NotificationResponseDto;
import ewha.capston.cockChat.domain.notification.service.NotificationService;
import ewha.capston.cockChat.global.config.auth.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/members/notifications")
    public ResponseEntity<List<NotificationResponseDto>> getMemberNotificationList(@AuthUser Member member){
        return notificationService.getMemberNotificationList(member);
    }


}
