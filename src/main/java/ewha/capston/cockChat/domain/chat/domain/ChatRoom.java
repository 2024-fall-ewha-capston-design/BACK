package ewha.capston.cockChat.domain.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false, unique = true)
    private String identifier;

    @Column(nullable = false)
    private Boolean roomType;

    @Column
    private Long password;

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false)
    private Boolean nicknameType;

    @Column
    private String chatRoomImgUrl;

    @Builder
    public ChatRoom(String identifier, Boolean roomType, Long password, String roomName, Boolean nicknameType, String chatRoomImgUrl){
        this.identifier = identifier;
        this.roomType = roomType;
        this.password = password;
        this.roomName = roomName;
        this.nicknameType = nicknameType;
        this.chatRoomImgUrl = chatRoomImgUrl;
    }



}
