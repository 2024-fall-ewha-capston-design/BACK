package ewha.capston.cockChat.global.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* member */
    NO_MEMBER_EXIST(HttpStatus.BAD_REQUEST , "가입되지 않은 회원입니다."),
    ALREADY_LIKED(HttpStatus.BAD_REQUEST,"이미 좋아요를 누른 스크랩입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"만료된 토큰입니다." ),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"유효하지 않은 토큰입니다." ),
    NON_LOGIN(HttpStatus.BAD_REQUEST,"로그인이 필요합니다." ),
    INVALID_MEMBER(HttpStatus.BAD_REQUEST,"접근 권한이 없는 회원입니다."),
    ALREADY_EXIST_NICKNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),

    /* chat room*/
    INVALID_ROOM(HttpStatus.BAD_REQUEST, "존재하지 않는 채팅방입니다."),

    /* chat */
    INVALID_MESSAGE_CONTENT(HttpStatus.BAD_REQUEST, "메시지 내용으로 공백이 들어왔습니다."),

    /* participant */
    INVALID_PARTICIPANT(HttpStatus.BAD_REQUEST,"잘못된 채팅 참여자입니다."),

    /* file */
    INPUT_IS_NULL(HttpStatus.BAD_REQUEST,"입력으로 null이 들어왔습니다."),
    FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"파일 삭제에 실패했습니다."),
    NO_CONTENT_EXIST(HttpStatus.BAD_REQUEST,"존재하지 않습니다.")
    ;

    private final HttpStatus status;
    private final String message;

}
