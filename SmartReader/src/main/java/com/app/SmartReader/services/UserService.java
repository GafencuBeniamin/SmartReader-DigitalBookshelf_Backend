package com.app.SmartReader.services;


import com.app.SmartReader.dtos.*;
import com.app.SmartReader.models.User;
import com.app.SmartReader.repositories.UserRepository;
import com.app.SmartReader.utils.enums.UserRole;
import com.app.SmartReader.utils.exceptions.AppException;
import com.app.SmartReader.utils.mappers.EntityToDtoMapper;
import com.app.SmartReader.utils.mappers.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.app.SmartReader.utils.enums.UserRole.MODERATOR;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public UserDto login(CredentialsDto credentialsDto) {
        User user = userRepository.findByUsername(credentialsDto.getUsername())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDto register(SignUpDto userDto) {
        Optional<User> optionalUserForEmail = userRepository.findByEmail(userDto.getEmail());

        if (optionalUserForEmail.isPresent()) {
            throw new AppException("Email already registered", HttpStatus.BAD_REQUEST);
        }

        Optional<User> optionalUserForUsername = userRepository.findByUsername(userDto.getUsername());

        if (optionalUserForUsername.isPresent()) {
            throw new AppException("Username already exists", HttpStatus.BAD_REQUEST);
        }

        User user = userMapper.signUpToUser(userDto);
        user.setRole(UserRole.USER);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.getPassword())));

        User savedUser = userRepository.save(user);

        return userMapper.toUserDto(savedUser);
    }
    public UserDto registerAdmin(SignUpDto userDto){
        Optional<User> optionalUserForEmail = userRepository.findByEmail(userDto.getEmail());

        if (optionalUserForEmail.isPresent()) {
            throw new AppException("Email already registered", HttpStatus.BAD_REQUEST);
        }

        Optional<User> optionalUserForUsername = userRepository.findByUsername(userDto.getUsername());

        if (optionalUserForUsername.isPresent()) {
            throw new AppException("Username already exists", HttpStatus.BAD_REQUEST);
        }

        User user = userMapper.signUpToUser(userDto);
        user.setRole(UserRole.ADMIN);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.getPassword())));

        User savedUser = userRepository.save(user);

        return userMapper.toUserDto(savedUser);
    }

    @Transactional
    public UserDto removeUser(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        userRepository.deleteByUsername(username);
        return userMapper.toUserDto(user);
    }

    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(user);
    }
    public List<UserDto> getAllUsers() {

        Iterable<User> users =userRepository.findAll();
        List<UserDto> results = new ArrayList<>();

        users.forEach(result -> results.add(EntityToDtoMapper.mapUserToDto(result)));
        return results;
    }
    public UserDto promoteUserToModerator(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        user.setRole(MODERATOR);
        userRepository.save(user);
        return EntityToDtoMapper.mapUserToDto(user);
    }

    public UserDto getMyDetails(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return EntityToDtoMapper.mapUserToDto(user);
    }

}
