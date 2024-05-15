package com.app.SmartReader.dtos;

import com.app.SmartReader.models.User;
import com.app.SmartReader.utils.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDto{
    private Integer id;
    private String username;
    private String email;
    private UserRole role;
    private String picture;
    private Set<User> friends;
    private String token;

//    public UserDto(User user) {
//        this.id=user.getId();
//        this.username=user.getUsername();
//        this.password=user.getPassword();
//        this.email= user.getEmail();
//        this.role = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
//        this.picture=user.getPicture();
//        this.friends=user.getFriends();
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return this.role;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
}
