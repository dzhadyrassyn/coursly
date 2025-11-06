package edu.coursly.app.service;

import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.model.ChatSession;
import edu.coursly.app.model.User;

public interface ChatSessionService {

    ChatSession getOrCreate(ChatRequest chatRequest, User user);
}
