package edu.coursly.app.service;

import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.dto.ChatResponse;

public interface ChatService {

    ChatResponse sendMessage(ChatRequest chatRequest);
}
