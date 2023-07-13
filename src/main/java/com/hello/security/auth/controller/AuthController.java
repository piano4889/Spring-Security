package com.hello.security.auth.controller;

import com.hello.security.common.SecurityUtil;
import com.hello.security.jwt.TokenProvider;
import com.hello.security.redis.service.RedisService;
import com.nimbusds.jwt.JWT;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.data.web.JsonPath;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final RedisService redisService;
    private final TokenProvider tokenProvider;
    private final SecurityUtil securityUtil;


    @GetMapping("/user/social")
    public ResponseEntity<?> sendKakaoATK(
            @RequestHeader(value = "Authorization") String inputATK
    ){

        logger.info("context id:{}", securityUtil.getCurrentName().get());

        logger.info("input jwt: {}" , inputATK);
        String atk = "";
        if (StringUtils.hasText(inputATK) && inputATK.startsWith("Bearer ")) {
            atk = inputATK.substring(7);
        }
        String id = tokenProvider.getAccountInfo(atk);
        logger.info("tokenProvider.getAccountInfo(inputATK) : {}",tokenProvider.getAccountInfo(atk));
        logger.info("redisService.getValues(email) : {}",redisService.getValues(id));

        if(redisService.getValues(id).isPresent()){
            return ResponseEntity.ok(redisService.getValues(id).get());
        }
        return new ResponseEntity<>("요청한 정보가 없습니다", HttpStatus.BAD_REQUEST);
    }
}
