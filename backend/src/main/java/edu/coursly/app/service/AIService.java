package edu.coursly.app.service;

import com.google.genai.types.Content;
import java.util.List;

public interface AIService {

    String sendMessage(String message);

    String sendChatConversation(List<Content> messages);
}
