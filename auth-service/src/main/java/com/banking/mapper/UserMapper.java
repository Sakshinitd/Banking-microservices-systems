package com.banking.mapper;

import com.banking.dto.request.UserRegisterReqDto;
import com.banking.entity.User;

import java.time.LocalDateTime;

public class UserMapper {

    public static User mapUserLoginReqToUser(UserRegisterReqDto userRegisterReqDto) {
        User user=new User();
        user.setEmail(userRegisterReqDto.getEmail());
        user.setPasswordHash(userRegisterReqDto.getPassword());
        user.setFullName(userRegisterReqDto.getFullName());
        user.setRoles(userRegisterReqDto.getRoles());
        user.setCreatedOn(LocalDateTime.now());
        return user;
    }

}
