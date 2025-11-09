package edu.coursly.app.service;

import edu.coursly.app.dto.ChatMessageResponse;
import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.dto.ChatResponse;
import edu.coursly.app.dto.ChatSessionResponse;
import java.util.List;

public interface ChatService {

    ChatResponse sendMessage(ChatRequest chatRequest);

    List<ChatSessionResponse> retrieveUserChatSessions();

    List<ChatMessageResponse> retrieveUserChatSessionMessages(Long chatSessionId);
}
