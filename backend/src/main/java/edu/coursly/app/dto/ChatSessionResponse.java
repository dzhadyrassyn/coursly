package edu.coursly.app.dto;

import java.time.Instant;

public record ChatSessionResponse(Long chatSessionId, String title, Instant created) {}
