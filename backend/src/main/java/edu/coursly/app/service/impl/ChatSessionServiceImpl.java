package edu.coursly.app.service.impl;

import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.model.ChatSession;
import edu.coursly.app.model.User;
import edu.coursly.app.repository.ChatSessionRepository;
import edu.coursly.app.service.ChatSessionService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ChatSessionServiceImpl implements ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;

    public ChatSessionServiceImpl(ChatSessionRepository chatSessionRepository) {
        this.chatSessionRepository = chatSessionRepository;
    }

    @Override
    public ChatSession getOrCreate(ChatRequest chatRequest, User user) {

        Objects.requireNonNull(chatRequest, "ChatRequest must not be null");
        Objects.requireNonNull(user, "User must not be null");
        return Optional.ofNullable(chatRequest.chatSessionId())
                .flatMap(chatSessionRepository::findById)
                .map(
                        it -> {
                            if (!Objects.equals(it.getUser().getId(), user.getId())) {
                                throw new AccessDeniedException(
                                        "Access denied for this chat session");
                            }
                            return it;
                        })
                .orElseGet(
                        () ->
                                chatSessionRepository.save(
                                        ChatSession.builder()
                                                .title(chatRequest.message())
                                                .user(user)
                                                .build()));
    }

    @Override
    public List<ChatSession> retrieveUserChatSessions(User user) {

        Objects.requireNonNull(user, "User must not be null");
        return chatSessionRepository.findAllByUser(user);
    }
}
