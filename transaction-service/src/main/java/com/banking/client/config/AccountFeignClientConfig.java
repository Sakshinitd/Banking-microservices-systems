package com.banking.client.config;


import feign.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AccountFeignClientConfig {

    @Bean
    public Logger.Level logLevel(){
        return Logger.Level.FULL;
    }

    @Bean
    public Request.Options options(){
        return new Request.Options(
                Duration.ofSeconds(3),
                Duration.ofSeconds(5),
                true
        );
    }

    @Bean
    public Retryer retryer(){
        return new Retryer.Default(
                1,
                3,
                3
        );
    }
}
