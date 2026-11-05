package com.banking.util;

import com.banking.exception.InvalidJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private static final String KEY =
            "my-super-secret-key-my-super-secret-key-123456";

    private final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(KEY.getBytes());

    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}