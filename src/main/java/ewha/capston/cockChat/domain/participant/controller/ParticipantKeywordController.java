package ewha.capston.cockChat.domain.participant.controller;

import ewha.capston.cockChat.domain.member.domain.Member;
import ewha.capston.cockChat.domain.participant.dto.KeywordInfoDto;
import ewha.capston.cockChat.domain.participant.dto.KeywordResponseDto;
import ewha.capston.cockChat.domain.participant.service.ParticipantKeywordService;
import ewha.capston.cockChat.global.config.auth.AuthUser;
import lombok.Getter;
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
    public ResponseEntity<KeywordResponseDto> createPositiveKeyword(@AuthUser Member member, @PathVariable(name = "participantId") Long participantId, @RequestBody KeywordInfoDto requestDto){
        return participantKeywordService.createPositiveKeyword(member, participantId, requestDto);
    }

    /* 부정 키워드 추가 */
    @PostMapping("/participants/{participantId}/negative-keywords")
    public ResponseEntity<KeywordResponseDto> createNegativeKeyword(@AuthUser Member member, @PathVariable(name = "participantId") Long participantId, @RequestBody KeywordInfoDto requestDto){
        return participantKeywordService.createNegativeKeyword(member, participantId, requestDto);
    }

    /* 긍정 키워드 목록 조회 */
    @GetMapping("/participants/{participantId}/positive-keywords")
    public ResponseEntity<List<KeywordResponseDto>> getPositiveKeywordListByParticipant(@AuthUser Member member, @PathVariable(name = "participantId") Long participantId){
        return participantKeywordService.getPositiveKeywordListByParticipant(member, participantId);
    }

    /* 부정 키워드 목록 조회 */
    @GetMapping("/participants/{participantId}/negative-keywords")
    public ResponseEntity<List<KeywordResponseDto>> getNegativeKeywordListByParticipant(@AuthUser Member member,  @PathVariable(name = "participantId") Long participantId){
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
