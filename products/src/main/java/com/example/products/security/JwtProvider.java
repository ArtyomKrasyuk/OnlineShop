package com.example.products.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class JwtProvider {
    private final SecretKey jwtAccessSecret;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
    }

    private Claims getClaims(@NonNull String token) {;
        try {
            return Jwts.parser().verifyWith(jwtAccessSecret).build().parseSignedClaims(token).getPayload();
        } catch (JwtException ex) {
            log.info("Invalid token");
            return null;
        }
    }
}
