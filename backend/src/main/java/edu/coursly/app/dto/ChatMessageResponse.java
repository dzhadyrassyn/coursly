package edu.coursly.app.dto;

import java.time.Instant;

public record ChatMessageResponse(
        Long messageId,
        String message,
        Instant created,
        Instant lastModified,
        Long chatSessionId) {}
