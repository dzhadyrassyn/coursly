package edu.coursly.app.service;

import edu.coursly.app.dto.ChatRequest;

public interface ChatService {

    void sendMessage(ChatRequest message);
}
