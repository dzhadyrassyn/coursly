package edu.coursly.app.service;

import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.model.ChatSession;
import edu.coursly.app.model.User;
import java.util.List;

public interface ChatSessionService {

    ChatSession getOrCreate(ChatRequest chatRequest, User user);

    List<ChatSession> retrieveUserChatSessions(User user);
}
