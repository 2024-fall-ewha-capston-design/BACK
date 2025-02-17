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

    @Column
    private String positiveKeywords;

    @Column
    private String negativeKeywords;

    @Column
    private String roomNickname;

    @Column
    private String participantImgUrl;

    @ManyToOne
    @JoinColumn(name = "chatRoom_id")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Participant(Boolean isOwner, String roomNickname, String participantImgUrl, ChatRoom chatRoom, Member member){
        this.isOwner = isOwner;
        this.roomNickname = roomNickname;
        this.participantImgUrl = participantImgUrl;
        this.chatRoom = chatRoom;
        this.member = member;
    }

}
