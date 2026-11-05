package com.banking.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/auth")
    public String authServiceFallback(){
        return "Auth service is temporarily unavailable";
    }
}
