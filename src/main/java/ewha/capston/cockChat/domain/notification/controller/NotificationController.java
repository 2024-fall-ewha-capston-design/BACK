package ewha.capston.cockChat.domain.notification.controller;

import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.notification.dto.NotificationResponseDto;
import ewha.capston.cockChat.domain.notification.service.NotificationService;
import ewha.capston.cockChat.global.config.auth.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class NotificationController {

    private final NotificationService notificationService;

    /* 사용자 맞춤 알림 목록 조회 */
    @GetMapping("/members/notifications")
    public ResponseEntity<List<NotificationResponseDto>> getMemberNotificationList(@AuthUser Member member){
        return notificationService.getMemberNotificationList(member);
    }

    /* 알림 읽음 처리 */
    @DeleteMapping("/members/notifications/{notificationId}/read")
    public ResponseEntity<Object> markNotificationAsRead(@AuthUser Member member, @PathVariable(name = "notificationId") String notificationId){
        return notificationService.markNotificationAsRead(member,notificationId);
    }


}
