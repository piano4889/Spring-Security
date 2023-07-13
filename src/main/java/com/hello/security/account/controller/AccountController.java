package com.hello.security.account.controller;

import com.hello.security.account.domain.Account;
import com.hello.security.account.service.CustomUserDetailsService;
import com.hello.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final CustomUserDetailsService memberService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @PostMapping("/create")
    public Account create(@RequestBody Account account) {
        logger.info("/create 실행 request account : {}", account);
        memberService.save(account);
        return account;
    }

    //{id}의 정보 확인
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getMyUserInfo(@PathVariable String id) throws Exception {

        return ResponseEntity.ok(memberService.getUserWithAuthorities(id)
                .orElseThrow(() -> new Exception("조회에 실패하였습니다.")));
    }

}
