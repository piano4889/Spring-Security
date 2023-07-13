package com.hello.security.jwt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hello.security.account.domain.Account;
import com.hello.security.jwt.TokenProvider;
import com.hello.security.jwt.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    /*
        인증(로그인)
        POST , /login 시 Filter 작동

    */
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        logger.info(">>> 로그인 시도 : JwtAuthenticationFilter.attemptAuthentication");
        //request 객체 속 id,password 확인을 위한 객체(Json을 특정 객체에 매핑)
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //Account 객체로 requestBody 내용 파싱
            Account account = objectMapper.readValue(request.getInputStream(), Account.class);

            //AuthenticationToken으로 변환 하는 과정에서 loadByUsername 메소드를 타서 검증
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(account.getId(), account.getPassword());

            //Authentication 객체가 만들어 진 후 Security Context 에 저장
            return authenticationManager.authenticate(authenticationToken);

        } catch (IOException e) {
            e.printStackTrace();
            logger.info(">>> request 객체 속 body에 로그인 정보가 없습니다.");
            return null;
        }
    }

    @SneakyThrows
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        logger.info(">>> 인증 완료 : JwtAuthenticationFilter.successfulAuthentication,로그인 ID : {}", authResult.getName());

        //토큰 발급 메소드 작성
        TokenDto tokenDto = tokenProvider.createToken(authResult);
        logger.info("\n >>> Access token : " + tokenDto.getAccessToken());

        //Refresh Token 쿠키에 저장, AccessToken Header에 추가
        response.addCookie(new Cookie("refreshToken",tokenDto.getRefreshToken()));
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
    }

}
