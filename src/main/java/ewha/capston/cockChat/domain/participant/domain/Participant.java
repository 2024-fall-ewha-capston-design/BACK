package ewha.capston.cockChat.domain.participant.domain;

import ewha.capston.cockChat.domain.chat.domain.ChatRoom;
import ewha.capston.cockChat.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    @Column
    private Boolean isOwner;

    /*

    테이블 분리하여 관리

    @Column
    private String positiveKeywords;

    @Column
    private String negativeKeywords;

     */
    @Column
    private String roomNickname;

    @Column
    private String participantImgUrl;

    @Column
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "chatRoom_id")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Participant(Boolean isOwner, Boolean isActive, String roomNickname, String participantImgUrl, ChatRoom chatRoom, Member member){
        this.isOwner = isOwner;
        this.isActive = isActive;
        this.roomNickname = roomNickname;
        this.participantImgUrl = participantImgUrl;
        this.chatRoom = chatRoom;
        this.member = member;
    }


    /* 키워드 설정 업데이트 */
    /*
    public void updateSettings(String positiveKeywords, String negativeKeywords){
        this.positiveKeywords = positiveKeywords;
        this.negativeKeywords = negativeKeywords;
    }
    */


    /* 방장 권한 업데이트 */
    public void updateIsOwner(Boolean isOwner){
        this.isOwner = isOwner;
    }

    /* 실명 사용 프로필 수정 */
    public void updateParticipantProfile(String roomNickname, String participantImgUrl){
        this.roomNickname = roomNickname;
        this.participantImgUrl = participantImgUrl;
    }

    /* 채팅방 탈퇴 */
    public void deactivateParticipant(){
        this.isActive = Boolean.FALSE;
    }
}
