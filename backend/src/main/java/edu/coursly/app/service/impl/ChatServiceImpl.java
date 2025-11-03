package edu.coursly.app.service.impl;

import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.dto.ChatResponse;
import edu.coursly.app.service.AIService;
import edu.coursly.app.service.ChatService;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

    private final AIService aiService;

    public ChatServiceImpl(AIService aiService) {
        this.aiService = aiService;
    }

    @Override
    public ChatResponse sendMessage(ChatRequest chatRequest) {
        return new ChatResponse(aiService.sendMessage(chatRequest.message()));
    }
}
