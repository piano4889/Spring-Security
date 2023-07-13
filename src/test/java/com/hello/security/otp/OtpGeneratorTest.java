package com.hello.security.otp;

import org.apache.commons.codec.binary.Base32;
import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class OtpGeneratorTest {

    @Test
    void generateOtp() throws NoSuchAlgorithmException, InvalidKeyException {

        //given
        String plainText = "thisisSecret";
        byte[] bytes = plainText.getBytes();
        byte[] encoded = new Base32().encode(bytes);
        System.out.println("encoded = " + encoded);

        OtpGenerator otpGenerator = new OtpGenerator();

        String otp = otpGenerator.generateOtp(plainText);

        System.out.println("otp = " + otp);

    }
}