package com.game.gameservermaster.utils;

import com.game.gameservermaster.config.GMConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {

    private String signingKey;

    JwtUtils(@Value("${jwt-secret}") String signingKey) {
        this.signingKey = signingKey;
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
            .setSigningKey(signingKey)
            .parseClaimsJws(token)
            .getBody();
    }

    public JwtBuilder buildClientToken(String username) {
        return buildClientToken(username, GMConstants.CLIENT_TOKEN_EXP_MS_CONNECT);
    }

    public JwtBuilder buildClientToken(String username, int expMs) {
        return Jwts.builder()
            .setSubject(username)
            .claim(GMConstants.JWT_ROLE_KEY, GMConstants.CLIENT_ROLE)
            .signWith(SignatureAlgorithm.HS256, signingKey)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expMs))
            .setId(UUID.randomUUID().toString());
    }



}
