package edu.coursly.app.service.impl;

import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.dto.ChatResponse;
import edu.coursly.app.service.ChatService;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

    @Override
    public ChatResponse sendMessage(ChatRequest chatRequest) {
        System.out.println("Sending message to Gemini AI...");
        return new ChatResponse(chatRequest.message());
    }
}
