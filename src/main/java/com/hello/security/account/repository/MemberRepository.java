package com.hello.security.account.repository;

import com.hello.security.account.domain.Account;
import com.hello.security.account.mapper.AccountMapper;
import com.hello.security.auth.domain.KakaoAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final AccountMapper accountMapper;

    public Account save(Account account) {
        accountMapper.createAccount(account);

        return account;
    }

    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accountMapper.findOneAccountById(id));
    }

    public List<String> findAuthoritiesById(String id) {
        return accountMapper.readAuthorities(id);
    }

    public List<String> readAuthoritiesFromSocial(String id) {
        return accountMapper.readAuthoritiesFromSocial(id);
    }

    public int saveSocialAccount(KakaoAccount kakaoAccount) {
        return accountMapper.saveSocialAccount(kakaoAccount);
    }

    public Optional<KakaoAccount> findKakaoAccountById(String email){
        return Optional.ofNullable(accountMapper.findKaKaoAccountById(email));
    }
}
