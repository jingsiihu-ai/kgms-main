package com.hjc.kgms;

import com.hjc.kgms.config.JwtProperties;
import com.hjc.kgms.security.AuthUserDetails;
import com.hjc.kgms.security.JwtTokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class JwtTokenServiceTest {

    @Test
    void shouldGenerateAndParseToken() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("THIS_IS_A_TEST_SECRET_KEY_FOR_KGMS_2026_03_12_1234567890");
        properties.setExpireSeconds(3600L);

        JwtTokenService jwtTokenService = new JwtTokenService(properties);
        AuthUserDetails user = new AuthUserDetails(
                1L,
                "principal",
                "pwd",
                "王园长",
                null,
                1,
                Arrays.asList("PRINCIPAL")
        );

        String token = jwtTokenService.createToken(user);

        Assertions.assertNotNull(token);
        Assertions.assertEquals("principal", jwtTokenService.getUsername(token));
        Assertions.assertTrue(jwtTokenService.getRoles(token).contains("PRINCIPAL"));
        Assertions.assertTrue(jwtTokenService.isTokenValid(token));
    }
}
