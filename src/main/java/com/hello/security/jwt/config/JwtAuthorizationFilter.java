package com.hello.security.jwt.config;

import com.hello.security.jwt.TokenProvider;
import com.hello.security.redis.service.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
    권한, 요청이 필요한 URL 호출 시 작동 되는 필터
    인가:권한 부여
        1. 요청이 JWT && Bearer인지 확인
        2. JWT 요청 일 시 AccessToken 검증
            2-1. AccessToken 정상일 때 DoFilter()
            2-2. AccessToken 만료 되었을 때 --> RefreshToken 검증
                2-2-1. RefreshToken 검증
            2-3. Access 토큰이 로그아웃 처리 된 토큰일 때
        3.로그인 처리(SecurityContext에 저장)
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
    private final RedisService redisService;
    private final TokenProvider tokenProvider;


    public JwtAuthorizationFilter(
            AuthenticationManager authenticationManager,
            RedisService redisService,
            TokenProvider tokenProvider) {
        super(authenticationManager);
        this.redisService = redisService;
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwt = resolveToken(request); //request 에서 jwt 가져오기
        String requestURI = request.getRequestURI(); //요청 URI
        AntPathRequestMatcher matcher = new AntPathRequestMatcher(requestURI);
        logger.info(">>> 인증(권한)이 필요한 로직 , 요청 URL : {}, JWT:{}", requestURI, jwt);
        // 유효성 검증 후 정상 토큰이면 SecurityContext 에 저장
        if (StringUtils.hasText(jwt)) { //JWT 일 때
            // 토큰이 정상이라면 Authentication 객체를 SecurityContext 에 저장
            try {
                if (tokenProvider.validateToken(jwt)) { // 토큰 검증
                    Authentication authentication = tokenProvider.getAuthentication(jwt); //권한 획득
                    logger.info("Input jwt = {}", jwt);

                    if (redisService.getValues(jwt).isPresent()) { //로그아웃(블럭) 처리된 액세스 토큰인지 확인
                        logger.info("Logout 처리된 AccessToken 입니다.");
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Logout 처리된 AccessToken 입니다.");
                        return;
                    }

                    SecurityContextHolder.getContext().setAuthentication(authentication); //Context 에 저장
                    chain.doFilter(request, response);
                    logger.info("Security Context에 '{}' 인증 정보를 저장했습니다", authentication.getName());
                }

            } catch (ExpiredJwtException e) {
                logger.info("(인증 요청) Access Token 만료된 Refresh Token 검증");
                logger.info("request.getCookies : {}", (Object) request.getCookies());

                if (request.getCookies() != null) {
                    Authentication authentication = tokenProvider.validateRefresh(request); //request 속 쿠키에 담겨져 있는 RefreshToken 검증
                    String accessToken = tokenProvider.getAccessToken(authentication);//Access Token 재발급

                    System.out.println("accessToken = " + accessToken);

                    response.addHeader("Authorization", "Bearer " + accessToken);

                    response.sendError(HttpServletResponse.SC_CONFLICT);

                    logger.info(">>> AccessToken 재발급 성공 \nheader : {}", response.getHeader("Authorization"));

                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "잘못된 접근입니다.");
                }
            }
        } else if (matcher.matches(request)) {
            //public api, security config에서 설정한 antMatcher와 일치시 필터 통과
            chain.doFilter(request, response);

        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "잘못된 접근입니다.");
        }
    }

    private String resolveToken(HttpServletRequest request) { //Header 에서 JWT 파싱
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
