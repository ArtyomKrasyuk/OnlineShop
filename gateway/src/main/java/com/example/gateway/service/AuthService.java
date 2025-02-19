package com.example.gateway.service;

import com.example.gateway.security.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.UUID;

@Service
public class AuthService {
    private final SecretKey jwtSecret;

    public AuthService(@Value("${jwt.secret.access}") String secret) {
        this.jwtSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(jwtSecret).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            System.out.println("Token expired " + expEx);
        } catch (UnsupportedJwtException unsEx) {
            System.out.println("Unsupported jwt " + unsEx);
        } catch (MalformedJwtException mjEx) {
            System.out.println("Malformed jwt " + mjEx);
        } catch (SignatureException sEx) {
            System.out.println("Invalid signature " + sEx);
        } catch (Exception e) {
            System.out.println("invalid token " + e);
        }
        return false;
    }

    public User getUserFromToken(String token){
        Claims claims = Jwts.parser().verifyWith(jwtSecret).build().parseSignedClaims(token).getPayload();
        String id = claims.get("id", String.class);
        return new User(UUID.fromString(id), claims.get("role", String.class));
    }
}
