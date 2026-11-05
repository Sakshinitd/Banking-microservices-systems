package com.banking.controller;

import com.banking.dto.request.UserLoginReqDto;
import com.banking.dto.request.UserRegisterReqDto;
import com.banking.dto.response.UserLoginResDto;
import com.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterReqDto userRegisterReqDto) {
        log.info("Inside register method");
        return ResponseEntity.ok(userService.registerUser(userRegisterReqDto));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResDto> login(@RequestBody UserLoginReqDto userLoginReqDto) {
        return ResponseEntity.ok(userService.loginUser(userLoginReqDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserLoginResDto> refresh(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(userService.refreshToken(request));
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String header) {
        return ResponseEntity.ok(userService.logoutUser(header));
    }

}
