package ewha.capston.cockChat.domain.penalty.event;

import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.participant.service.ParticipantService;
import ewha.capston.cockChat.domain.penalty.domain.Penalty;
import ewha.capston.cockChat.domain.penalty.dto.PenaltyResponseDto;
import ewha.capston.cockChat.domain.penalty.service.PenaltyService;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PenaltyRankBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;
    private final ParticipantService participantService;
    private final PenaltyService penaltyService;

    @Scheduled(fixedRate = 3000) // 3초마다 실행
    public void sendPenaltyRankToEachParticipant() {
        List<Participant> participantList = participantService.findAllByIsActiveTrue();

        log.info("📋 활성 참여자 ID 목록 = {}", participantList.stream()
                .map(Participant::getParticipantId)
                .toList());

        /* 활성 상태의 채팅 참여자가 없는 경우 */
        if(participantList.isEmpty()) return;

        for(Participant participant : participantList){
            List<Penalty> top3PenaltyList = penaltyService.findTop3ByReceiverId(participant.getParticipantId());

            if(top3PenaltyList.isEmpty()) continue;

            List<PenaltyResponseDto> penaltyList = top3PenaltyList.stream()
                    .map(PenaltyResponseDto::of)
                    .collect(Collectors.toList());

            log.info("✅ [PenaltyService] receiverId={}, 결과개수={}", participant.getParticipantId(), penaltyList.size());

            /* 벌점 상위 3명 전달 */
            messagingTemplate.convertAndSend(
                    "/topic/penalty/" + participant.getParticipantId(),
                    penaltyList
            );


        }
    }


}
