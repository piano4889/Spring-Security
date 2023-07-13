package com.hello.security.auth.service;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public interface KakaoService {

    Map<String, Object> getProperties(OAuth2User oAuth2User);
    Map<String, Object> getKakaoAccount(OAuth2User oAuth2User);


}
