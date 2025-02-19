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
    private Boolean isSecretChatRoom;

    @Column
    private Long password;

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false)
    private Boolean isAnonymousChatRoom;

    @Column
    private String chatRoomImgUrl;

    @Builder
    public ChatRoom(String identifier, Boolean isSecretChatRoom, Long password, String roomName, Boolean isAnonymousChatRoom, String chatRoomImgUrl){
        this.identifier = identifier;
        this.isSecretChatRoom = isSecretChatRoom;
        this.password = password;
        this.roomName = roomName;
        this.isAnonymousChatRoom = isAnonymousChatRoom;
        this.chatRoomImgUrl = chatRoomImgUrl;
    }



}
