package com.banking.service;

import com.banking.dto.request.UserLoginReqDto;
import com.banking.dto.request.UserRegisterReqDto;
import com.banking.dto.response.UserLoginResDto;

import java.util.Map;

public interface UserService {
    String registerUser(UserRegisterReqDto userRegisterReqDto);
    UserLoginResDto loginUser(UserLoginReqDto userLoginReqDto);
    UserLoginResDto refreshToken(Map<String,String> request);
    String logoutUser(String header);
}
