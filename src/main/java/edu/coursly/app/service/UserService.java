package edu.coursly.app.service;

import edu.coursly.app.dto.UserRegistrationRequest;
import edu.coursly.app.dto.UserRegistrationResponse;

public interface UserService {

    void registerUser(UserRegistrationRequest userRegistrationRequest);

    UserRegistrationResponse findByUsername(String username);
}
