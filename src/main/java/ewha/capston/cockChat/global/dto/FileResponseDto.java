package ewha.capston.cockChat.global.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FileResponseDto {
    private String data;
    public static FileResponseDto of(String data){
        return new FileResponseDto(data);
    }
}
