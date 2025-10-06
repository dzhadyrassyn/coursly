package edu.coursly.app.service.impl;

import edu.coursly.app.dto.UserRegistrationRequest;
import edu.coursly.app.dto.UserRegistrationResponse;
import edu.coursly.app.exception.UserAlreadyExistsException;
import edu.coursly.app.exception.UserNotFoundException;
import edu.coursly.app.mapper.UserMapper;
import edu.coursly.app.model.User;
import edu.coursly.app.repository.UserRepository;
import edu.coursly.app.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public void registerUser(UserRegistrationRequest userRegistrationRequest) {
        Objects.requireNonNull(userRegistrationRequest, "DTO is required");
        Objects.requireNonNull(userRegistrationRequest.username(), "username is required");
        Objects.requireNonNull(userRegistrationRequest.password(), "password is required");

        if (userRepository.findByUsername(userRegistrationRequest.username()).isPresent()) {
            throw new UserAlreadyExistsException("Username is already registered");
        }

        User user = userMapper.toEntity(userRegistrationRequest);
        user.setPassword(passwordEncoder.encode(userRegistrationRequest.password()));
        userRepository.save(user);
    }

    @Override
    public UserRegistrationResponse findByUsername(String username) {
        Objects.requireNonNull(username, "username is required");

        return userRepository.findByUsername(username).map(userMapper::toDto).orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
