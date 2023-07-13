package com.hello.security.jwt.config;

import com.hello.security.jwt.TokenProvider;
import com.hello.security.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    //유효한 자격증명을 제공하지 않고 접근하려고 할 때, 401 Unauthorized 에러를 리턴 하는 클래스
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        //로그인 한 사람들 중에서도 권한 유무에 따라 다른 Response.status return
        logger.info("인증 실패");
    }

}
