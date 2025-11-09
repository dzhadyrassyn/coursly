package edu.coursly.app.service.impl;

import edu.coursly.app.dto.ChatMessageResponse;
import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.dto.ChatResponse;
import edu.coursly.app.dto.ChatSessionResponse;
import edu.coursly.app.mapper.ChatMessageMapper;
import edu.coursly.app.mapper.ChatSessionMapper;
import edu.coursly.app.model.ChatSession;
import edu.coursly.app.model.User;
import edu.coursly.app.service.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatServiceImpl implements ChatService {

    private final AIService aiService;
    private final ChatSessionService chatSessionService;
    private final ChatMessageService chatMessageService;
    private final UserService userService;
    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;

    public ChatServiceImpl(
            AIService aiService,
            ChatSessionService chatSessionService,
            ChatMessageService chatMessageService,
            UserService userService,
            ChatSessionMapper chatSessionMapper,
            ChatMessageMapper chatMessageMapper) {
        this.aiService = aiService;
        this.chatSessionService = chatSessionService;
        this.chatMessageService = chatMessageService;
        this.userService = userService;
        this.chatSessionMapper = chatSessionMapper;
        this.chatMessageMapper = chatMessageMapper;
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

    @Override
    public List<ChatSessionResponse> retrieveUserChatSessions() {

        User user = userService.getCurrentUser();
        return chatSessionService.retrieveUserChatSessions(user).stream()
                .map(chatSessionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessageResponse> retrieveUserChatSessionMessages(Long chatSessionId) {

        Objects.requireNonNull(chatSessionId, "chatSessionId cannot be null");

        User user = userService.getCurrentUser();
        return chatMessageService.retrieveMessages(chatSessionId, user).stream()
                .map(chatMessageMapper::toDto)
                .collect(Collectors.toList());
    }
}
