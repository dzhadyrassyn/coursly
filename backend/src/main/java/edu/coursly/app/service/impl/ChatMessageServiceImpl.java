package edu.coursly.app.service.impl;

import edu.coursly.app.model.ChatMessage;
import edu.coursly.app.model.ChatSession;
import edu.coursly.app.model.User;
import edu.coursly.app.model.enums.MessageSenderType;
import edu.coursly.app.repository.ChatMessageRepository;
import edu.coursly.app.service.ChatMessageService;
import java.util.List;
import java.util.Objects;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageServiceImpl(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public void saveUserMessage(String content, ChatSession session) {

        ChatMessage message =
                ChatMessage.builder()
                        .content(content)
                        .chatSession(session)
                        .sender(MessageSenderType.USER)
                        .build();

        chatMessageRepository.save(message);
    }

    @Override
    public void saveAIMessage(String content, ChatSession session) {

        ChatMessage message =
                ChatMessage.builder()
                        .content(content)
                        .chatSession(session)
                        .sender(MessageSenderType.AI)
                        .build();

        chatMessageRepository.save(message);
    }

    @Override
    public List<ChatMessage> retrieveMessages(Long chatSessionId, User user) {

        List<ChatMessage> messages =
                chatMessageRepository.findAllByChatSession_IdOrderByCreatedAsc(chatSessionId);
        if (!messages.isEmpty()
                && !Objects.equals(
                        messages.getFirst().getChatSession().getUser().getId(), user.getId())) {
            throw new AccessDeniedException("You do not have access to this chat session");
        }

        return messages;
    }
}
