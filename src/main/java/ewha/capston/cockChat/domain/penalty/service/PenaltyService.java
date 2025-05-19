package ewha.capston.cockChat.domain.penalty.service;

import ewha.capston.cockChat.ai.dto.ChatAnalysisRequestDto;
import ewha.capston.cockChat.ai.dto.ChatAnalysisResult;
import ewha.capston.cockChat.domain.chat.domain.Chat;
import ewha.capston.cockChat.domain.chat.mongo.MongoChatRepository;
import ewha.capston.cockChat.domain.chat.service.ChatService;
import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.participant.domain.ParticipantNegativeKeyword;
import ewha.capston.cockChat.domain.participant.service.ParticipantKeywordService;
import ewha.capston.cockChat.domain.participant.service.ParticipantService;
import ewha.capston.cockChat.domain.penalty.domain.Penalty;
import ewha.capston.cockChat.domain.penalty.repository.PenaltyRepository;
import ewha.capston.cockChat.global.exception.CustomException;
import ewha.capston.cockChat.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PenaltyService {

    //private final MongoChatRepository chatRepository;
    //private final ChatService chatService;
    private final MongoChatRepository chatRepository;
    private final ParticipantKeywordService keywordService;
    private final ParticipantService participantService;
    private final PenaltyRepository penaltyRepository;

    /* 패널티 부여 */
    public void applyPenaltiesFromAnalysis(List<ChatAnalysisResult> analysisResults) {
        for(ChatAnalysisResult result : analysisResults){
           // Chat chat = chatService.findById(result.getChatId());
            Chat chat = chatRepository.findById(result.getChatId()).orElseThrow(()->new CustomException(ErrorCode.INVALID_CHAT_ID));
            Participant receiver = participantService.findById(Long.parseLong(result.getParticipantId()));
            Participant offender = participantService.findById(chat.getParticipantId());

            // 본인에게는 벌점을 부여하지 않음.
            if(receiver.equals(offender)) continue;
            ParticipantNegativeKeyword negativeKeyword = keywordService.findNegativeKeywordById(Long.parseLong(result.getKeywordId()));

            /* 존재하지 않는 키워드인 경우, 벌점 부과 X */
            if(negativeKeyword.equals(null)) continue;;

            Penalty penalty = findByReceiverAndOffender(receiver,offender);

            /* 이미 존재하는 벌점 부여 대상자라면 */
            if(penalty != null){

                // 벌점 부여
                penalty.increasePenalty(negativeKeyword.getPenaltyScore());
            }

            /* 새로운 벌점 부여 대상자라면 */
            else{

                // 새로운 패널티 생성
                penaltyRepository.save(Penalty.builder()
                        .receiverParticipant(receiver)
                        .offenderParticipant(offender)
                        .penaltyCount(negativeKeyword.getPenaltyScore())
                        .build());
            }
        }

        log.info("패널티 부여 완료");
    }

    public Penalty findByReceiverAndOffender(Participant receiver, Participant offender){
        Penalty penalty = null;
        return  penaltyRepository.findByReceiverParticipantAndOffenderParticipant(receiver, offender)
                .orElse(null);
    }

    /* 벌점 상위 3명 조회 */
    public List<Penalty> findTop3ByReceiverId(Long receiverId){
        // Pageable topThree = (Pageable) PageRequest.of(0, 3);
        return penaltyRepository.findTop3ByReceiverParticipant_ParticipantIdAndOffenderParticipant_ParticipantIdNotOrderByPenaltyCountDesc(receiverId, receiverId);
        //return penaltyRepository.findTop3ByReceiverIdOrderByPenaltyCountDescExcludeSelf(receiverId, topThree);
    }
}
