package com.hello.security.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // 키 - 밸류 설정
    public void setValues(String id, String token,Duration duration) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(id, token, duration);
    }

    // 키 값으로 밸류 가져오기
    public Optional<String> getValues(String id){
        ValueOperations<String, String> value = redisTemplate.opsForValue();
        return Optional.ofNullable(value.get(id));
    }

    // 키-벨류 삭제
    public void delValues(String id) {
        redisTemplate.delete(id);
    }
}
