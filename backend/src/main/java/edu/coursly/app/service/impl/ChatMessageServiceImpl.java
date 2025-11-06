package edu.coursly.app.service.impl;

import edu.coursly.app.model.ChatMessage;
import edu.coursly.app.model.ChatSession;
import edu.coursly.app.model.enums.MessageSenderType;
import edu.coursly.app.repository.ChatMessageRepository;
import edu.coursly.app.service.ChatMessageService;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageServiceImpl(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public ChatMessage saveUserMessage(String content, ChatSession session) {
        ChatMessage message =
                ChatMessage.builder()
                        .content(content)
                        .chatSession(session)
                        .sender(MessageSenderType.USER)
                        .build();

        return chatMessageRepository.save(message);
    }

    @Override
    public ChatMessage saveAIMessage(String content, ChatSession session) {
        ChatMessage message =
                ChatMessage.builder()
                        .content(content)
                        .chatSession(session)
                        .sender(MessageSenderType.AI)
                        .build();

        return chatMessageRepository.save(message);
    }
}
