package edu.coursly.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserLoginRequest(
        @NotNull(message = "Username is required")
        @Size(min = 6, message = "Username must be at least 6 characters long")
        String username,
        @NotNull(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password) {}
