package edu.coursly.app.dto;

import java.time.Instant;

public record ChatResponse(String message, Long chatSessionId, Instant created) {}
