package edu.coursly.app.service;

import edu.coursly.app.dto.UserRegistrationRequest;

public interface UserService {

    void registerUser(UserRegistrationRequest userRegistrationRequest);
}
