package edu.coursly.app.service;

import edu.coursly.app.dto.UserRegistrationRequest;
import edu.coursly.app.model.entity.User;

public interface UserService {

    void registerUser(UserRegistrationRequest userRegistrationRequest);

    User getCurrentUser();
}
