package edu.coursly.app.service.impl;

import com.google.genai.types.Content;
import edu.coursly.app.dto.ChatMessageResponse;
import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.dto.ChatResponse;
import edu.coursly.app.dto.ChatSessionResponse;
import edu.coursly.app.mapper.ChatMessageMapper;
import edu.coursly.app.mapper.ChatSessionMapper;
import edu.coursly.app.mapper.GeminiContentMapper;
import edu.coursly.app.model.dto.ChatContent;
import edu.coursly.app.model.entity.ChatSession;
import edu.coursly.app.model.entity.User;
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
    private final GeminiContentMapper geminiContentMapper;

    public ChatServiceImpl(
            AIService aiService,
            ChatSessionService chatSessionService,
            ChatMessageService chatMessageService,
            UserService userService,
            ChatSessionMapper chatSessionMapper,
            ChatMessageMapper chatMessageMapper,
            GeminiContentMapper geminiContentMapper) {
        this.aiService = aiService;
        this.chatSessionService = chatSessionService;
        this.chatMessageService = chatMessageService;
        this.userService = userService;
        this.chatSessionMapper = chatSessionMapper;
        this.chatMessageMapper = chatMessageMapper;
        this.geminiContentMapper = geminiContentMapper;
    }

    @Override
    @Transactional
    public ChatResponse sendMessage(ChatRequest chatRequest) {

        User user = userService.getCurrentUser();

        ChatSession chatSession = chatSessionService.getOrCreate(chatRequest, user);

        chatMessageService.saveUserMessage(chatRequest.message(), chatSession);

        List<ChatContent> last10Messages =
                chatMessageService.retrieveLast10Messages(chatSession.getId());

        List<Content> geminiMessages =
                last10Messages.stream().map(geminiContentMapper::toGeminiContent).toList();

        String aiResponse = aiService.sendChatConversation(geminiMessages);
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
