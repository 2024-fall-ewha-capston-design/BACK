package ewha.capston.cockChat.domain.participant.controller;

import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.participant.dto.request.NegativeKeywordRequestDto;
import ewha.capston.cockChat.domain.participant.dto.request.PositiveKeywordRequestDto;
import ewha.capston.cockChat.domain.participant.dto.response.NegativeKeywordResponseDto;
import ewha.capston.cockChat.domain.participant.dto.response.PositiveKeywordResponseDto;
import ewha.capston.cockChat.domain.participant.service.ParticipantKeywordService;
import ewha.capston.cockChat.global.config.auth.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ParticipantKeywordController {
    private final ParticipantKeywordService participantKeywordService;

    /* 긍정 키워드 추가 */
    @PostMapping("/participants/{participantId}/positive-keywords")
    public ResponseEntity<PositiveKeywordResponseDto> createPositiveKeyword(@AuthUser Member member, @PathVariable(name = "participantId") Long participantId, @RequestBody PositiveKeywordRequestDto requestDto){
        return participantKeywordService.createPositiveKeyword(member, participantId, requestDto);
    }

    /* 부정 키워드 추가 */
    @PostMapping("/participants/{participantId}/negative-keywords")
    public ResponseEntity<NegativeKeywordResponseDto> createNegativeKeyword(@AuthUser Member member, @PathVariable(name = "participantId") Long participantId, @RequestBody NegativeKeywordRequestDto requestDto){
        return participantKeywordService.createNegativeKeyword(member, participantId, requestDto);
    }

    /* 긍정 키워드 목록 조회 */
    @GetMapping("/participants/{participantId}/positive-keywords")
    public ResponseEntity<List<PositiveKeywordResponseDto>> getPositiveKeywordListByParticipant(@AuthUser Member member, @PathVariable(name = "participantId") Long participantId){
        return participantKeywordService.getPositiveKeywordListByParticipant(member, participantId);
    }

    /* 부정 키워드 목록 조회 */
    @GetMapping("/participants/{participantId}/negative-keywords")
    public ResponseEntity<List<NegativeKeywordResponseDto>> getNegativeKeywordListByParticipant(@AuthUser Member member, @PathVariable(name = "participantId") Long participantId){
        return participantKeywordService.getNegativeKeywordListByParticipant(member, participantId);
    }

    /* 긍정 키워드 삭제 */
    @DeleteMapping("/participants/{participantId}/positive-keywords/{keywordId}")
    public ResponseEntity deletePositiveKeyword(@AuthUser Member member, @PathVariable(name = "participantId") Long participantId, @PathVariable(name = "keywordId") Long keywordId){
        return participantKeywordService.deletePositiveKeyword(member, participantId, keywordId);
    }

    /* 부정 키워드 삭제 */
    @DeleteMapping("/participants/{participantId}/negative-keywords/{keywordId}")
    public ResponseEntity deleteNegativeKeyword(@AuthUser Member member, @PathVariable(name = "participantId") Long participantId, @PathVariable(name = "keywordId") Long keywordId){
        return participantKeywordService.deleteNegativeKeyword(member, participantId, keywordId);
    }
}
