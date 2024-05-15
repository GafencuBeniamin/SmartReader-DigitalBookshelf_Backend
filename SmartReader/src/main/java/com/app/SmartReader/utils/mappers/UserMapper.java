package com.app.SmartReader.utils.mappers;


import com.app.SmartReader.dtos.SignUpDto;

import com.app.SmartReader.dtos.UserDto;

import com.app.SmartReader.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    @Mapping(target = "password", ignore = true)
    User signUpToUser(SignUpDto signUpDto);

}
