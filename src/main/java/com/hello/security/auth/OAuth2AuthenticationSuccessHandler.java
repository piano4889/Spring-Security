package com.hello.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hello.security.auth.domain.KakaoAccount;
import com.hello.security.jwt.TokenProvider;
import com.hello.security.jwt.dto.TokenDto;
import com.hello.security.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.token.Token;
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    private final TokenProvider tokenProvider;
    private final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);
    private final JavaMailSender javaMailSender;
    private final RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("request URI : {}", request.getRequestURL());
        logger.info("request URI : {}", request.getHeaderNames());
        logger.info("request:{}", request.getParameter("code"));

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        redisService.setValues(oAuth2User.getName(), request.getParameter("code"), Duration.ofMillis(60000));
        logger.info("로그인 성공 유저 : {}", oAuth2User.getName());

        TokenDto tokenDto = tokenProvider.createToken(oAuth2User);

        Map<String, Object> kakao_account = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
        String email = (String) kakao_account.get("email");
//        sendEmail(email);

        Map<String, Object> properties = (Map<String, Object>) oAuth2User.getAttributes().get("properties");
        String nickname = (String) properties.get("nickname");

        String url = makeRedirectUrl(tokenDto,email,nickname);

        writeTokenResponse(response,tokenDto);

        if (response.isCommitted()) {
            logger.debug("이미 응답된 response입니다.");
            return;
        }

        logger.info("accessToken : {}", tokenDto.getAccessToken());
        logger.info("refreshToken : {}", tokenDto.getRefreshToken());
        getRedirectStrategy().sendRedirect(request, response, url);

    }

    private void sendEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("이메일 테스트");
        message.setText("이메일 테스트");
        message.setTo(email);

        javaMailSender.send(message);
    }

    private String makeRedirectUrl(TokenDto token,String email,String nickname) {
        return UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/kakao")
                .queryParam("accessToken", token.getAccessToken())
                .queryParam("refreshToken", token.getRefreshToken())
                .queryParam("email", email)
                .queryParam("nickname", nickname)
                .build().toUriString();
    }

    private void writeTokenResponse(HttpServletResponse response, TokenDto token)
            throws IOException {
        response.addCookie(new Cookie("refreshToken", token.getRefreshToken()));
        response.setContentType("application/json;charset=UTF-8");

    }
}


