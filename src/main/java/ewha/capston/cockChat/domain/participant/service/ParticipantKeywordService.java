package ewha.capston.cockChat.domain.participant.service;

import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.participant.domain.Participant;
import ewha.capston.cockChat.domain.participant.domain.ParticipantNegativeKeyword;
import ewha.capston.cockChat.domain.participant.domain.ParticipantPositiveKeyword;
import ewha.capston.cockChat.domain.participant.dto.request.NegativeKeywordRequestDto;
import ewha.capston.cockChat.domain.participant.dto.request.PositiveKeywordRequestDto;
import ewha.capston.cockChat.domain.participant.dto.response.NegativeKeywordResponseDto;
import ewha.capston.cockChat.domain.participant.dto.response.PositiveKeywordResponseDto;
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
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipantKeywordService {

    private final ParticipantRepository participantRepository;
    private final ParticipantPositiveKeywordRepository positiveKeywordRepository;
    private final ParticipantNegativeKeywordRepository negativeKeywordRepository;

    /* 긍정 키워드 설정 */
    public ResponseEntity<PositiveKeywordResponseDto> createPositiveKeyword(Member member, Long participantId, PositiveKeywordRequestDto requestDto) {
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
                .body(PositiveKeywordResponseDto.of(positiveKeyword));
    }

    /* 부정 키워드 설정 */
    public ResponseEntity<NegativeKeywordResponseDto> createNegativeKeyword(Member member, Long participantId, NegativeKeywordRequestDto requestDto) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PARTICIPANT));
        if(!participant.getMember().equals(member)) throw new CustomException(ErrorCode.INVALID_MEMBER);
        if(negativeKeywordRepository.countByParticipant(participant) > 5L) throw new CustomException(ErrorCode.EXCEEDED_KEYWORD_LIMIT);
        ParticipantNegativeKeyword negativeKeyword = negativeKeywordRepository.save(
                ParticipantNegativeKeyword.builder()
                        .participant(participant)
                        .penaltyScore(requestDto.getPenaltyScore())
                        .content(requestDto.getKeyword())
                        .build()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NegativeKeywordResponseDto.of(negativeKeyword));
    }

    /* 긍정 키워드 목록 조회 */
    public ResponseEntity<List<PositiveKeywordResponseDto>> getPositiveKeywordListByParticipant(Member member, Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PARTICIPANT));
        if(!participant.getMember().equals(member)) throw new CustomException(ErrorCode.INVALID_MEMBER);
        if(participant.getIsActive().equals(Boolean.FALSE)) throw new CustomException(ErrorCode.NOT_A_PARTICIPANT);
        List<ParticipantPositiveKeyword> positiveKeywordList = positiveKeywordRepository.findAllByParticipant(participant);
        return ResponseEntity.status(HttpStatus.OK)
                .body(positiveKeywordList.stream().map(positiveKeyword -> PositiveKeywordResponseDto.of(positiveKeyword)).collect(Collectors.toList()));
    }

    /* 부정 키워드 목록 조회 */
    public ResponseEntity<List<NegativeKeywordResponseDto>> getNegativeKeywordListByParticipant(Member member, Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_PARTICIPANT));
        if (!participant.getMember().equals(member)) throw new CustomException(ErrorCode.INVALID_MEMBER);
        if (participant.getIsActive().equals(Boolean.FALSE)) throw new CustomException(ErrorCode.NOT_A_PARTICIPANT);
        List<ParticipantNegativeKeyword> negativeKeywordList = negativeKeywordRepository.findAllByParticipant(participant);
        return ResponseEntity.status(HttpStatus.OK)
                .body(negativeKeywordList.stream().map(negativeKeyword -> NegativeKeywordResponseDto.of(negativeKeyword)).collect(Collectors.toList()));

    }

    /* 긍정 키워드 삭제 */
    public ResponseEntity deletePositiveKeyword(Member member, Long participantId, Long keywordId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PARTICIPANT));
        if(!participant.getMember().equals(member)) throw new CustomException(ErrorCode.INVALID_MEMBER);
        if(participant.getIsActive().equals(Boolean.FALSE)) throw new CustomException(ErrorCode.NOT_A_PARTICIPANT);
        ParticipantPositiveKeyword positiveKeyword = positiveKeywordRepository.findById(keywordId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_KEYWORD));
        if(!positiveKeyword.getParticipant().equals(participant)) throw new CustomException(ErrorCode.NO_PERMISSION);

        positiveKeywordRepository.delete(positiveKeyword);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /* 부정 키워드 삭제 */
    public ResponseEntity deleteNegativeKeyword(Member member, Long participantId, Long keywordId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PARTICIPANT));
        if(!participant.getMember().equals(member)) throw new CustomException(ErrorCode.INVALID_MEMBER);
        if(participant.getIsActive().equals(Boolean.FALSE)) throw new CustomException(ErrorCode.NOT_A_PARTICIPANT);
        ParticipantNegativeKeyword negativeKeyword = negativeKeywordRepository.findById(keywordId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_KEYWORD));
        if(!negativeKeyword.getParticipant().equals(participant)) throw new CustomException(ErrorCode.NO_PERMISSION);

        negativeKeywordRepository.delete(negativeKeyword);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /* 부정 키워드 id로 조회 */
    public ParticipantNegativeKeyword findNegativeKeywordById(Long keywordId) {
        return negativeKeywordRepository.findById(keywordId)
                .orElseThrow(null);
    }
}
