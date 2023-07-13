package com.hello.security.jwt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {

    private String id;
    private String password;
}
