package com.hello.security.otp;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class OtpGenerator {
    private static final int SECRET_SIZE = 10;
    private static final int INTERVAL = 30;
    private static final int NUMBER_OF_DIGITS = 6;

    public String generateOtp(String base32Secret) throws NoSuchAlgorithmException, InvalidKeyException {
        long time = System.currentTimeMillis() / 1000 / INTERVAL;
        byte[] secret = decodeBase32(base32Secret);
        byte[] data = ByteBuffer.allocate(8).putLong(time).array();
        byte[] hash = hmacSha1(secret, data);
        int offset = hash[hash.length - 1] & 0xf;
        int binary =
                ((hash[offset] & 0x7f) << 24) |
                        ((hash[offset + 1] & 0xff) << 16) |
                        ((hash[offset + 2] & 0xff) << 8) |
                        (hash[offset + 3] & 0xff);
        int otp = binary % (int) Math.pow(10, NUMBER_OF_DIGITS);
        return String.format("%0" + NUMBER_OF_DIGITS + "d", otp);
    }

    private byte[] decodeBase32(String base32) {
        return new Base32().decode(base32);
    }

    private byte[] hmacSha1(byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(key, "RAW"));
        return mac.doFinal(data);
    }


}
