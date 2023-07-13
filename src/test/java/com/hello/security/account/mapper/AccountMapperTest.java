package com.hello.security.account.mapper;

import com.hello.security.account.domain.Account;
import com.hello.security.redis.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;

@SpringBootTest
class AccountMapperTest {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisService redisService;

    @Test
    void redistest(){
        redisService.setValues("key", "value", Duration.ofMinutes(5));

    }

    @Test
    void getredisValue(){
        System.out.println("redisService.getValues(\"key\") = " + redisService.getValues("key"));
    }

    @Test
    void selectTest(){
        //given
        String name = "test";
        //when
        Account account = accountMapper.findOneAccountById(name);
        //then
        System.out.println("account = " + account.toString());
    }

    @Test
    void insertTest(){
        //given
        Account account = new Account();
        account.setId("tester");
        account.setPassword(passwordEncoder.encode("1234"));
        account.setAccountNonExpired(true);
        account.setAccountNonLocked(true);
        account.setCredentialsNonExpired(true);
        account.setEnabled(true);
        //when

        int result = accountMapper.createAccount(account);

        System.out.println("result = " + result);
    }
}