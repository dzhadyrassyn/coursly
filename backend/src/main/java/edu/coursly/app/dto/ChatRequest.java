package edu.coursly.app.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(@NotBlank(message = "Message is required") String message) {}
