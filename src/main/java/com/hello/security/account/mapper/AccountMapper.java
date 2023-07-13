package com.hello.security.account.mapper;

import com.hello.security.account.domain.Account;
import com.hello.security.auth.domain.KakaoAccount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccountMapper {
    Account findOneAccountById(String username);

    int createAccount(Account Account);

    List<String> readAuthorities(String id);



    // social Account
    int saveSocialAccount(KakaoAccount kakaoAccount);

    List<String> readAuthoritiesFromSocial(String id);

    KakaoAccount findKaKaoAccountById(String email);
}
