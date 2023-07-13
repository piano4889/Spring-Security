package com.hello.security.jwt;

import com.hello.security.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class JwtRepository {

    private final RedisService redisService;

    public void insertRefreshToken(String id, String refreshToken, Duration duration) {
        redisService.setValues(id, refreshToken, duration);
    }

    public Boolean getRefreshToken(String id, String refreshToken) throws Exception {
        String validRefreshToken = redisService.getValues(id).orElseThrow(() -> new Exception("요청한 정보가 올바르지 않습니다."));

        return validRefreshToken.equals(refreshToken);
    }

}
