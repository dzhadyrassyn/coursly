package edu.coursly.app.service.impl;

import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.dto.ChatResponse;
import edu.coursly.app.model.ChatSession;
import edu.coursly.app.model.User;
import edu.coursly.app.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatServiceImpl implements ChatService {

    private final AIService aiService;
    private final ChatSessionService chatSessionService;
    private final ChatMessageService chatMessageService;
    private final UserService userService;

    public ChatServiceImpl(
            AIService aiService,
            ChatSessionService chatSessionService,
            ChatMessageService chatMessageService,
            UserService userService) {
        this.aiService = aiService;
        this.chatSessionService = chatSessionService;
        this.chatMessageService = chatMessageService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public ChatResponse sendMessage(ChatRequest chatRequest) {

        User user = userService.getCurrentUser();
        ChatSession chatSession = chatSessionService.getOrCreate(chatRequest, user);
        chatMessageService.saveUserMessage(chatRequest.message(), chatSession);

        String aiResponse = aiService.sendMessage(chatRequest.message());
        chatMessageService.saveAIMessage(aiResponse, chatSession);

        return new ChatResponse(aiResponse, chatSession.getId());
    }
}
