package edu.coursly.app.service.impl;

import edu.coursly.app.mapper.ChatContentJsonMapper;
import edu.coursly.app.model.dto.ChatContent;
import edu.coursly.app.model.dto.TextPart;
import edu.coursly.app.model.entity.ChatMessage;
import edu.coursly.app.model.entity.ChatSession;
import edu.coursly.app.model.entity.User;
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
    private final ChatContentJsonMapper chatContentJsonMapper;

    public ChatMessageServiceImpl(
            ChatMessageRepository chatMessageRepository,
            ChatContentJsonMapper chatContentJsonMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatContentJsonMapper = chatContentJsonMapper;
    }

    @Override
    public void saveUserMessage(String content, ChatSession session) {

        ChatContent chatContent = new ChatContent("user", List.of(new TextPart(content)));
        ChatMessage message =
                ChatMessage.builder()
                        .contentJson(chatContentJsonMapper.toJson(chatContent))
                        .chatSession(session)
                        .sender(MessageSenderType.USER)
                        .build();

        chatMessageRepository.save(message);
    }

    @Override
    public void saveAIMessage(String content, ChatSession session) {

        ChatContent chatContent = new ChatContent("model", List.of(new TextPart(content)));
        ChatMessage message =
                ChatMessage.builder()
                        .contentJson(chatContentJsonMapper.toJson(chatContent))
                        .chatSession(session)
                        .sender(MessageSenderType.MODEL)
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

    @Override
    public List<ChatContent> retrieveLast10Messages(Long chatSessionId) {

        return chatMessageRepository
                .findTop10ByChatSession_IdOrderByCreatedAsc(chatSessionId)
                .stream()
                .map(msg -> chatContentJsonMapper.fromJson(msg.getContentJson()))
                .toList();
    }
}
