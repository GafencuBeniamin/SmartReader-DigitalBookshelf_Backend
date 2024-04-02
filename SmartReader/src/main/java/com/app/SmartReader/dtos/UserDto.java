package com.app.SmartReader.dtos;

import com.app.SmartReader.utils.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDto {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private UserRole role;
    private String profile;
}
