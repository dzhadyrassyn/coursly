package edu.coursly.app.service.impl;

import edu.coursly.app.dto.UserRegistrationRequest;
import edu.coursly.app.exception.UserAlreadyExistsException;
import edu.coursly.app.mapper.UserMapper;
import edu.coursly.app.model.User;
import edu.coursly.app.model.enums.Role;
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
        Objects.requireNonNull(userRegistrationRequest.getUsername(), "username is required");
        Objects.requireNonNull(userRegistrationRequest.getPassword(), "password is required");

        if (userRepository.findByUsername(userRegistrationRequest.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username is already registered");
        }

        User user = userMapper.toEntity(userRegistrationRequest);
        user.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));
        user.setRole(Role.ROLE_STUDENT);
        userRepository.save(user);
    }
}
