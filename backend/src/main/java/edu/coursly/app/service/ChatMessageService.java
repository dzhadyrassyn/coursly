package edu.coursly.app.service;

import edu.coursly.app.model.dto.ChatContent;
import edu.coursly.app.model.entity.ChatMessage;
import edu.coursly.app.model.entity.ChatSession;
import edu.coursly.app.model.entity.User;
import java.util.List;

public interface ChatMessageService {

    void saveUserMessage(String content, ChatSession session);

    void saveAIMessage(String content, ChatSession session);

    List<ChatMessage> retrieveMessages(Long chatSessionId, User user);

    List<ChatContent> retrieveLast10Messages(Long id);
}
