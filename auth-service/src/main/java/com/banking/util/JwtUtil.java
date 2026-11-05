package com.banking.util;

import com.banking.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final String KEY="my-super-secret-key-my-super-secret-key-123456";
    private final SecretKey SECRET_KEY= Keys.hmacShaKeyFor(KEY.getBytes());

    public String generateToken(User user) {
        final long EXPIRATION_TIME =  1000*60*60;
        return Jwts
                .builder()
                .subject(user.getEmail())
                .claim("userId",user.getUserId())
                .claim("authorities",user.getRoles())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token, String username) {
        return isValidUserName(token, username) && !isTokenExpired(token);
    }

    public boolean isValidUserName(String token, String username) {
        return extractClaims(token).getSubject().equals(username);
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public Claims extractClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long extractReminingTime(String token) {
        return extractClaims(token).getExpiration().getTime() - System.currentTimeMillis();
    }
}
