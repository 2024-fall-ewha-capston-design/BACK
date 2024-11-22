package ewha.capston.cockChat.ai.controller;

import ewha.capston.cockChat.ai.dto.NegativeChatRequestDto;
import ewha.capston.cockChat.ai.service.FastApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final FastApiService fastApiService;

    @PostMapping("/filter-negative-chat")
    public ResponseEntity<Object> filterNegativeChat(@RequestBody NegativeChatRequestDto requestDto){
        return fastApiService.filterNegativeChat(requestDto);
    }

    /*
    @GetMapping("/test-call")
    public ResponseEntity<Object> getHelloWorld(){
        return ResponseEntity.status(HttpStatus.OK)
                .body("hello world");
    }

     */
}
