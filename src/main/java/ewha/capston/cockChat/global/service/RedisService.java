package ewha.capston.cockChat.global.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    /* 만료 시간이 지나면, 자동 삭제 */
    public void setValues(String key, String value, Long timeOut){
        redisTemplate.opsForValue().set(key,value,timeOut, TimeUnit.MILLISECONDS);
    }

    @Transactional
    public String getValues(String key){
        return redisTemplate.opsForValue().get(key);
    }

    @Transactional
    public Boolean checkValues(String key){
        return redisTemplate.hasKey(key);
    }
}
