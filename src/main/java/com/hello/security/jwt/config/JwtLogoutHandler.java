package com.hello.security.jwt.config;

import com.hello.security.jwt.TokenProvider;
import com.hello.security.redis.service.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.time.Duration;

import static com.hello.security.jwt.config.JwtAuthorizationFilter.AUTHORIZATION_HEADER;

//TODO SuccessHandler 로 구현해보기
@RequiredArgsConstructor
public class JwtLogoutHandler  {

    private static final Logger logger = LoggerFactory.getLogger(JwtLogoutHandler.class);
    private final RedisService redisService;
    private final TokenProvider tokenProvider;

    @SneakyThrows
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        /*
            로그아웃 필터에 의해 호출
            1. request 의 액세스 토큰 검증 (만료 되었는지 아닌지)
            1-1 만료된 토큰일 시 response 에 Fail 처리
            2. 정상적인 액세스 토큰 일 시 Reids 속 Refresh 삭제 및 요청 액세스 토큰 블락 처리
            3. Context 의 사용자 정보 삭제 및 쿠키 삭제
         */
        logger.info(">>>> JwtLogoutHandler 실행");

        //Header 의 JWT 정보 파싱
        String jwt = resolveToken(request);

        logger.info("input Jwt: {}", jwt);
        //AccessToken 검증 실패 시 400 return

        if (!tokenProvider.validateToken(jwt)) {
            response.sendError(409, "올바르지 않은 접근입니다.");
        }

    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
