package edu.coursly.app.controller;

import edu.coursly.app.dto.ChatMessageResponse;
import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.dto.ChatResponse;
import edu.coursly.app.dto.ChatSessionResponse;
import edu.coursly.app.service.ChatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(ApiPaths.API_V1_CHAT)
    public ChatResponse sendMessage(@Valid @RequestBody ChatRequest message) {
        return chatService.sendMessage(message);
    }

    @GetMapping(ApiPaths.API_V1_CHAT_SESSIONS)
    public List<ChatSessionResponse> retrieveUserChatSessions() {
        return chatService.retrieveUserChatSessions();
    }

    @GetMapping(ApiPaths.API_V1_CHAT_MESSAGES)
    public List<ChatMessageResponse> retrieveUserChatSessionMessages(
            @PathVariable @NotNull Long id) {
        return chatService.retrieveUserChatSessionMessages(id);
    }
}
