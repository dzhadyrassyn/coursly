package edu.coursly.app.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.dto.ChatResponse;
import edu.coursly.app.dto.ChatSessionResponse;
import edu.coursly.app.service.ChatService;
import edu.coursly.app.service.impl.CustomUserDetailsServiceImpl;
import edu.coursly.app.util.JwtUtil;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private ChatService chatService;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private JwtUtil jwtUtil;

    @MockitoBean private CustomUserDetailsServiceImpl customUserDetailsService;

    @Test
    @DisplayName("Should return chat response when request is valid")
    void shouldReturnChatResponse() throws Exception {
        ChatRequest request = new ChatRequest("Hello, AI!", null);
        ChatResponse response = new ChatResponse("Echo: Hello, AI!", null);

        when(chatService.sendMessage(request)).thenReturn(response);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(ApiPaths.API_V1_CHAT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Echo: Hello, AI!"));
    }

    @Test
    @DisplayName("Should return 400 if message is empty")
    void shouldReturnBadRequestOnInvalidInput() throws Exception {
        ChatRequest invalidRequest = new ChatRequest("", null);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(ApiPaths.API_V1_CHAT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return list of user chat sessions (HTTP 200)")
    void retrieveUserChatSessions_success() throws Exception {
        // given
        var response1 =
                new ChatSessionResponse(1L, "Session 1", Instant.parse("2025-11-03T10:00:00Z"));
        var response2 =
                new ChatSessionResponse(2L, "Session 2", Instant.parse("2025-11-03T12:00:00Z"));

        when(chatService.retrieveUserChatSessions()).thenReturn(List.of(response1, response2));

        // when + then
        mockMvc.perform(get(ApiPaths.API_V1_CHAT_SESSIONS).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].chatSessionId").value(1L))
                .andExpect(jsonPath("$[0].title").value("Session 1"))
                .andExpect(jsonPath("$[1].chatSessionId").value(2L))
                .andExpect(jsonPath("$[1].title").value("Session 2"));
    }

    @Test
    @DisplayName("Should return empty list when no sessions found (HTTP 200)")
    void retrieveUserChatSessions_emptyList() throws Exception {
        // given
        when(chatService.retrieveUserChatSessions()).thenReturn(List.of());

        // when + then
        mockMvc.perform(get(ApiPaths.API_V1_CHAT_SESSIONS).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
