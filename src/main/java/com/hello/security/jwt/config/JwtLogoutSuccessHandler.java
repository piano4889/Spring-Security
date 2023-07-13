package com.hello.security.jwt.config;

import com.hello.security.jwt.TokenProvider;
import com.hello.security.redis.service.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

import static com.hello.security.jwt.config.JwtAuthorizationFilter.AUTHORIZATION_HEADER;

@RequiredArgsConstructor
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(JwtLogoutSuccessHandler.class);
    private final RedisService redisService;
    private final TokenProvider tokenProvider;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info(">>>> JwtLogoutSuccessHandler 실행");

        //Header 의 JWT 정보 파싱
        String jwt = resolveToken(request);
        logger.info("input Jwt: {}", jwt);

        if(StringUtils.hasText(jwt)){
            try{

                //인증 객체 가져오기
                authentication = tokenProvider.getAuthentication(jwt);
                //Redis 속 Refresh 토큰 삭제
                if (redisService.getValues(authentication.getName()).isPresent()) {
                    redisService.delValues(authentication.getName());
                }
                //접근 했던 Access 토큰 블럭 처리
                redisService.setValues(jwt, "logout", Duration.ofMillis(tokenProvider.getExpiration(jwt)));
            }
            catch (ExpiredJwtException e){
                logger.info("예외 발생 --> JWT 만료");
                response.sendError(409,"text is inline plz ");
            };
        } else{
            logger.info("예외 발생 --> 잘못된 접근");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"잘못된 접근입니다.");
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

