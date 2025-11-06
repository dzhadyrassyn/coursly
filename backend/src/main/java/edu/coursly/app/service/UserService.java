package edu.coursly.app.service;

import edu.coursly.app.dto.UserRegistrationRequest;
import edu.coursly.app.model.User;

public interface UserService {

    void registerUser(UserRegistrationRequest userRegistrationRequest);

    User getCurrentUser();
}
