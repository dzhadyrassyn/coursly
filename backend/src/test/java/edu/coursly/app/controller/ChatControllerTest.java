package edu.coursly.app.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.dto.ChatResponse;
import edu.coursly.app.service.ChatService;
import edu.coursly.app.service.impl.CustomUserDetailsServiceImpl;
import edu.coursly.app.util.JwtUtil;
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
        ChatRequest request = new ChatRequest("Hello, AI!");
        ChatResponse response = new ChatResponse("Echo: Hello, AI!");

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
        ChatRequest invalidRequest = new ChatRequest("");

        mockMvc.perform(
                        MockMvcRequestBuilders.post(ApiPaths.API_V1_CHAT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
