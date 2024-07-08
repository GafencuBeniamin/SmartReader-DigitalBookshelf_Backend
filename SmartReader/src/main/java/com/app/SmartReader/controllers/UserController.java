package com.app.SmartReader.controllers;

import com.app.SmartReader.dtos.UserDto;
import com.app.SmartReader.repositories.UserRepository;
import com.app.SmartReader.services.UserService;
import com.app.SmartReader.utils.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @GetMapping("/single/{username}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<Object> getUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(userRepository.findByUsername(username));
    }
    @GetMapping("/myDetails")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<Object> getMyDetails(){
        return ResponseEntity.ok(userService.getMyDetails(getLoggedInUserDetails().getUsername()));
    }
    @PutMapping("/changeUserRole/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> changeUserRole(@PathVariable String username, @RequestBody UserRole role){
        return ResponseEntity.ok(userService.changeUserRole(username, role));
    }
    @PutMapping("/updateMyDetails")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<Object> updateMyDetails(@RequestBody UserDto userDto){
        return ResponseEntity.ok(userService.updateMyDetails(getLoggedInUserDetails().getId(), userDto));
    }
    public UserDto getLoggedInUserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDto){
            return (UserDto) authentication.getPrincipal();
        }
        return null;
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> deleteUser(@PathVariable String username){
        return ResponseEntity.ok(userService.removeUser(username));
    }

}
