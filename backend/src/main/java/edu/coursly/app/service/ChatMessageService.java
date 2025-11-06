package edu.coursly.app.service;

import edu.coursly.app.model.ChatMessage;
import edu.coursly.app.model.ChatSession;

public interface ChatMessageService {

    ChatMessage saveUserMessage(String content, ChatSession session);

    ChatMessage saveAIMessage(String content, ChatSession session);
}
