package edu.coursly.app.service;

import edu.coursly.app.model.ChatMessage;
import edu.coursly.app.model.ChatSession;
import edu.coursly.app.model.User;
import java.util.List;

public interface ChatMessageService {

    void saveUserMessage(String content, ChatSession session);

    void saveAIMessage(String content, ChatSession session);

    List<ChatMessage> retrieveMessages(Long chatSessionId, User user);
}
