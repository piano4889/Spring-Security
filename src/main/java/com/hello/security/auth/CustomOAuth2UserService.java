package com.hello.security.auth;

import com.hello.security.account.repository.MemberRepository;
import com.hello.security.auth.domain.KakaoAccount;
import com.hello.security.jwt.config.JwtAuthorizationFilter;
import com.hello.security.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final HttpSession httpSession;
    private final MemberRepository memberRepository;
    private final RedisService redisService;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        logger.info("Oauth2 로그인 시작 : {}",userRequest.getClientRegistration().getRegistrationId());


        OAuth2User oAuth2User = super.loadUser(userRequest);
        logger.info("Oath User.getAuthorities : {}", oAuth2User.getAuthorities());
        logger.info("Oath User.attributes : {}", oAuth2User.getAttributes());
        Map<String, Object> attributes = oAuth2User.getAttributes();

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String nickname = (String) properties.get("nickname");
        String email = (String) kakaoAccount.get("email");

        Instant instant = userRequest.getAccessToken().getExpiresAt();
        long toEpochMilli = instant.toEpochMilli();


        logger.info("카카오 : {}", kakaoAccount);
        logger.info("attributes : {}", attributes);
        logger.info("attributes.id : {}", attributes.get("id"));
        logger.info("닉네임 : {}", nickname);
        logger.info("이메일 : {}", email);

        if(!memberRepository.findKakaoAccountById(email).isPresent()){
            memberRepository.saveSocialAccount(new KakaoAccount(email, nickname));
        }

        //TODO kakao계정 회원화 완료. 이후 Oauth success에서 JWT 발급 하기
        return new DefaultOAuth2User((getAuthorities(email)),attributes,"id");
    }


    public Collection<GrantedAuthority> getAuthorities(String email) {
        List<String> string_authorities = memberRepository.readAuthoritiesFromSocial(email);
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String authority : string_authorities) {
            authorities.add(new SimpleGrantedAuthority(authority));
        }
        return authorities;
    }

}
