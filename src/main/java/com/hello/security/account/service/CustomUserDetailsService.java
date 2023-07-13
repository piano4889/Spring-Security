package com.hello.security.account.service;

import com.hello.security.account.domain.Account;
import com.hello.security.account.repository.MemberRepository;
import com.hello.security.jwt.JwtRepository;
import com.hello.security.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<Account> optionalMember = memberRepository.findById(id);
        optionalMember.orElseThrow(() -> new UsernameNotFoundException("조회된 멤버가 없습니다"));

        //return Member 객체 반환
        return User.builder()
                .username(optionalMember.get().getId())
                .password(optionalMember.get().getPassword())
                .authorities(getAuthorities(id))
                .build();
        }

    @Transactional
    public void save(Account account) { //회원 가입
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        memberRepository.save(account);
    }

    public Collection<GrantedAuthority> getAuthorities(String id) {
        List<String> string_authorities = memberRepository.findAuthoritiesById(id);
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String authority : string_authorities) {
            authorities.add(new SimpleGrantedAuthority(authority));
        }
        return authorities;
    }

    public Optional<Account> getUserWithAuthorities(String id) {
        return memberRepository.findById(id);
    }

}
