package com.hello.security.auth.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoAccount {
    private String email;
    private String nickname;

    public KakaoAccount(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
