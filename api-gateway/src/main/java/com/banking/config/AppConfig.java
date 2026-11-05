package com.banking.config;

import com.banking.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class AppConfig {

    private final JwtUtil jwtUtil;

    public AppConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public KeyResolver userKeyResolver() {

        return exchange -> {
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Mono.just(
                        Objects.requireNonNull(exchange.getRequest()
                                        .getRemoteAddress())
                                .getAddress()
                                .getHostAddress()
                );
            }

            String token = authHeader.substring(7);
            String username = jwtUtil.extractClaims(token).getSubject();
            return Mono.just(username);
        };
    }
}
