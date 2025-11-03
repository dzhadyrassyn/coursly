package edu.coursly.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.dto.ChatResponse;
import edu.coursly.app.service.AIService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock private AIService aiService;

    @InjectMocks private ChatServiceImpl chatService;

    @Test
    @DisplayName("Should return ChatResponse with AI message")
    void sendMessage_ShouldReturnChatResponse() {
        // given
        ChatRequest chatRequest = new ChatRequest("Hello AI");
        when(aiService.sendMessage("Hello AI")).thenReturn("Hello human!");

        // when
        ChatResponse response = chatService.sendMessage(chatRequest);

        // then
        assertNotNull(response);
        assertEquals("Hello human!", response.message());
        verify(aiService, times(1)).sendMessage("Hello AI");
    }

    @Test
    @DisplayName("Should propagate exception if AI service fails")
    void sendMessage_ShouldThrowException_WhenAIServiceFails() {
        ChatRequest chatRequest = new ChatRequest("Hello");
        when(aiService.sendMessage(anyString())).thenThrow(new RuntimeException("AI error"));

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> chatService.sendMessage(chatRequest));

        assertEquals("AI error", exception.getMessage());
        verify(aiService, times(1)).sendMessage("Hello");
    }
}
