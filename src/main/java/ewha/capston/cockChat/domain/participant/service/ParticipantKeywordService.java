package ewha.capston.cockChat.domain.participant.service;

import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.participant.domain.ParticipantNegativeKeyword;
import ewha.capston.cockChat.domain.participant.domain.ParticipantPositiveKeyword;
import ewha.capston.cockChat.domain.participant.dto.KeywordInfoDto;
import ewha.capston.cockChat.domain.participant.dto.KeywordResponseDto;
import ewha.capston.cockChat.domain.participant.repository.ParticipantNegativeKeywordRepository;
import ewha.capston.cockChat.domain.participant.repository.ParticipantPositiveKeywordRepository;
import ewha.capston.cockChat.domain.participant.repository.ParticipantRepository;
import ewha.capston.cockChat.global.exception.CustomException;
import ewha.capston.cockChat.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipantKeywordService {

    private final ParticipantRepository participantRepository;
    private final ParticipantPositiveKeywordRepository positiveKeywordRepository;
    private final ParticipantNegativeKeywordRepository negativeKeywordRepository;

    /* 긍정 키워드 설정 */
    public ResponseEntity<KeywordResponseDto> createPositiveKeyword(Member member, Long participantId, KeywordInfoDto requestDto) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(()-> new CustomException(ErrorCode.INVALID_PARTICIPANT));
        if(!participant.getMember().equals(member)) throw new CustomException(ErrorCode.INVALID_MEMBER);
        if(positiveKeywordRepository.countByParticipant(participant) > 5L) throw new CustomException(ErrorCode.EXCEEDED_KEYWORD_LIMIT);
        ParticipantPositiveKeyword positiveKeyword = positiveKeywordRepository.save(
                ParticipantPositiveKeyword.builder()
                        .participant(participant)
                        .content(requestDto.getKeyword())
                        .build()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(KeywordResponseDto.of(positiveKeyword.getPositiveKeywordId(), positiveKeyword.getContent()));
    }

    /* 부정 키워드 설정 */
    public ResponseEntity<KeywordResponseDto> createNegativeKeyword(Member member, Long participantId, KeywordInfoDto requestDto) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PARTICIPANT));
        if(!participant.getMember().equals(member)) throw new CustomException(ErrorCode.INVALID_MEMBER);
        if(negativeKeywordRepository.countByParticipant(participant) > 5L) throw new CustomException(ErrorCode.EXCEEDED_KEYWORD_LIMIT);
        ParticipantNegativeKeyword negativeKeyword = negativeKeywordRepository.save(
                ParticipantNegativeKeyword.builder()
                        .participant(participant)
                        .content(requestDto.getKeyword())
                        .build()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(KeywordResponseDto.of(negativeKeyword.getNegativeKeywordId(),negativeKeyword.getContent()));
    }
}
