package com.app.SmartReader.controllers;

import com.app.SmartReader.dtos.UserDto;
import com.app.SmartReader.repositories.UserRepository;
import com.app.SmartReader.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @GetMapping("/")
//    public String goHome(){
//        return "This is publicly accesible within needing auth";
//    }
//    @PostMapping("/save")
//    public ResponseEntity<Object> saveUser(@RequestBody User user){
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        User result = userRepository.save(user);
//        if (result.getId() > 0){
//            return ResponseEntity.ok("User was saved");
//        }
//        return ResponseEntity.status(404).body("Error: User not saved");
//    }
    @GetMapping("/all")
    public ResponseEntity<Object> getAllUsers(){
        return ResponseEntity.ok(userRepository.findAll());
    }
    @GetMapping("/single")
    public ResponseEntity<Object> getUserByUsername(String username){
        return ResponseEntity.ok(userRepository.findAll());
    }
    @GetMapping("/myDetails")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<Object> getMyDetails(){
        return ResponseEntity.ok(userRepository.findByUsername(getLoggedInUserDetails().getUsername()));
    }
    public UserDto getLoggedInUserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDto){
            return (UserDto) authentication.getPrincipal();
        }
        return null;
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable String username){
        return ResponseEntity.ok(userService.removeUser(username));
    }
}
