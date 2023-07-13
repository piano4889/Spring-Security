package com.hello.security.common;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class TokenProviderTest {

    @Test
    public void hs512(){
        long milliseconds = (1000 * 60 * 60 * 24 * 7);
        long seconds = TimeUnit.MILLISECONDS.toDays(milliseconds);
        System.out.println("seconds = " + seconds);
    }
}