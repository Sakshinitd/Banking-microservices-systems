package com.banking.service.impl;

import com.banking.dto.request.UserLoginReqDto;
import com.banking.dto.request.UserRegisterReqDto;
import com.banking.dto.response.UserLoginResDto;
import com.banking.entity.User;
import com.banking.exception.InvalidUserCredentials;
import com.banking.exception.UserAlreadyExist;
import com.banking.exception.UserNotFoundException;
import com.banking.repository.UserRepository;
import com.banking.service.UserService;
import com.banking.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static com.banking.mapper.UserMapper.mapUserLoginReqToUser;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    @Override
    public String registerUser(UserRegisterReqDto userRegisterReqDto) {
        if(userRepository.findByEmail(userRegisterReqDto.getEmail()).isPresent())
            throw new UserAlreadyExist("User already exist. Please try to login with that email.");
        userRegisterReqDto.setPassword(bCryptPasswordEncoder.encode(userRegisterReqDto.getPassword()));
        userRepository.save(mapUserLoginReqToUser(userRegisterReqDto));
        return "User registered successfully!";
    }

    @Override
    public UserLoginResDto loginUser(UserLoginReqDto userLoginReqDto) {

        User user = userRepository.findByEmail(userLoginReqDto.getEmail()).orElseThrow(()-> new UserNotFoundException("User not found!"));
        if(!bCryptPasswordEncoder.matches(userLoginReqDto.getPassword(), user.getPasswordHash())) {
            throw new InvalidUserCredentials("Invalid username or password!");
        }
        //Store refresh token in cache
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate
                .opsForValue()
                .set(
                        "refresh:"+refreshToken,
                        userLoginReqDto.getEmail(),
                        Duration.ofDays(7)
                );

        return new UserLoginResDto(jwtUtil.generateToken(user), refreshToken);
    }

    @Override
    public UserLoginResDto refreshToken(Map<String,String> request) {
        String refreshToken = request.get("refreshToken");
        //Get refresh token from cache
        String email = (String) redisTemplate.opsForValue().get("refresh:" + refreshToken);
        if(email == null) {
            throw new InvalidUserCredentials("Invalid refresh token!");
        }

        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("User not found from the refresh token!"));
        return new UserLoginResDto(jwtUtil.generateToken(user), refreshToken);
    }

    @Override
    public String logoutUser(String header) {
        String token = header.substring(7).trim();
        long extractReminingTime = jwtUtil.extractReminingTime(token);
        redisTemplate.opsForValue().set(
                "backlist:"+token,
                "true",
                Duration.ofMillis(extractReminingTime)
        );
        return "User logout successfully!";
    }
}
