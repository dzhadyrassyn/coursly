package edu.coursly.app.controller;

import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public void sendMessage(@Valid @RequestBody ChatRequest message) {
        chatService.sendMessage(message);
    }
}
