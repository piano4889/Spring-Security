package com.hello.security.auth.service;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class KakaoServiceImpl implements KakaoService{
    @Override
    public Map<String, Object> getProperties(OAuth2User oAuth2User) {
        return (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
    }

    @Override
    public Map<String, Object> getKakaoAccount(OAuth2User oAuth2User) {
        return null;
    }
}
