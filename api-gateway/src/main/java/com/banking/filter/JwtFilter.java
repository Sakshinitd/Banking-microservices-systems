package com.banking.filter;

import com.banking.exception.InvalidJwtException;
import com.banking.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

@Component
public class JwtFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    public JwtFilter(JwtUtil jwtUtil, StringRedisTemplate redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    private static final Logger log =
            LoggerFactory.getLogger(JwtFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        log.info("Path : {}", path);

        if (path.startsWith("/api/v1/auth/login") ||
                path.startsWith("/api/v1/auth/register")) {

            return chain.filter(exchange);
        }

        String authHeader =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        String blacklist =
                redisTemplate.opsForValue()
                        .get("backlist:" + token);

        if (blacklist != null &&
                blacklist.equals("true")) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }
        String username;
        Authentication authentication;
        Claims claims;
        try {
            claims = jwtUtil.extractClaims(token);
        } catch (JwtException e) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        if(claims.getExpiration().before(new Date())){

            log.info("Token expired");

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }


        username = claims.getSubject();

        List<GrantedAuthority> authorities =
                ((List<?>) claims.get("authorities"))
                        .stream()
                        .map(Object::toString)
                        .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                        .toList();


        log.info("Username : {}", username);
        log.info("Authorities : {}", authorities);

        authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                );

        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header("X-User-Email", username)
                .build();

        ServerWebExchange modifiedExchange = exchange
                .mutate()
                .request(request)
                .build();

        return chain.filter(modifiedExchange)
                .contextWrite(
                        ReactiveSecurityContextHolder.withAuthentication(authentication)
                );
    }
}
