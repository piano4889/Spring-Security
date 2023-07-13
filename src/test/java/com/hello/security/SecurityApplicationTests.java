package com.hello.security;

import org.apache.commons.codec.binary.Base32;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SecurityApplicationTests {

	@Test
	void contextLoads() {

		Base32 base32 = new Base32();

	}

}
