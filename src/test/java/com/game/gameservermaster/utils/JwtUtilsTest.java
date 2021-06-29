package com.game.gameservermaster.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

class JwtUtilsTest {

    private JwtUtils jwtUtils = new JwtUtils("secret");

    @Test
    void testInvalidSignature(){
        String token = jwtUtils.buildClientToken("username")
            .signWith(SignatureAlgorithm.HS256, "badkey")
            .compact();
        Assertions.assertThrows(SignatureException.class, () -> jwtUtils.getClaims(token));
    }

    @Test
    void testTokenExpired(){
        String token = jwtUtils.buildClientToken("username")
            .setExpiration(new Date(System.currentTimeMillis() - 10000))
            .compact();
        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtUtils.getClaims(token));
    }

}
