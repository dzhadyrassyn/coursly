package edu.coursly.app.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.dto.ChatResponse;
import edu.coursly.app.dto.ChatSessionResponse;
import edu.coursly.app.mapper.ChatSessionMapper;
import edu.coursly.app.model.ChatSession;
import edu.coursly.app.model.User;
import edu.coursly.app.service.AIService;
import edu.coursly.app.service.ChatMessageService;
import edu.coursly.app.service.ChatSessionService;
import edu.coursly.app.service.UserService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock private AIService aiService;
    @Mock private ChatSessionService chatSessionService;
    @Mock private ChatMessageService chatMessageService;
    @Mock private UserService userService;
    @Mock private ChatSessionMapper chatSessionMapper;

    @InjectMocks private ChatServiceImpl chatService;

    private User user;
    private ChatSession chatSession;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("daniyar").build();
        chatSession = ChatSession.builder().id(42L).title("Session 1").user(user).build();
    }

    @Test
    @DisplayName("Should save user and AI messages and return ChatResponse")
    void sendMessage_success() {
        // given
        ChatRequest chatRequest = new ChatRequest("Hello AI!", 42L);
        when(userService.getCurrentUser()).thenReturn(user);
        when(chatSessionService.getOrCreate(chatRequest, user)).thenReturn(chatSession);
        when(aiService.sendMessage("Hello AI!")).thenReturn("Hi human!");

        // when
        ChatResponse response = chatService.sendMessage(chatRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo("Hi human!");
        assertThat(response.chatSessionId()).isEqualTo(42L);

        verify(chatMessageService).saveUserMessage("Hello AI!", chatSession);
        verify(chatMessageService).saveAIMessage("Hi human!", chatSession);
        verify(aiService).sendMessage("Hello AI!");
        verifyNoMoreInteractions(chatMessageService, aiService);
    }

    @Test
    @DisplayName("Should retrieve and map all user chat sessions")
    void retrieveUserChatSessions_success() {
        // given
        when(userService.getCurrentUser()).thenReturn(user);
        when(chatSessionService.retrieveUserChatSessions(user)).thenReturn(List.of(chatSession));

        ChatSessionResponse dto =
                new ChatSessionResponse(42L, "Session 1", chatSession.getCreated());
        when(chatSessionMapper.toDto(chatSession)).thenReturn(dto);

        // when
        List<ChatSessionResponse> result = chatService.retrieveUserChatSessions();

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().chatSessionId()).isEqualTo(42L);
        assertThat(result.getFirst().title()).isEqualTo("Session 1");

        verify(userService).getCurrentUser();
        verify(chatSessionService).retrieveUserChatSessions(user);
        verify(chatSessionMapper).toDto(chatSession);
    }

    @DisplayName("Should propagate exception when AIService fails")
    @Test
    void sendMessage_aiServiceThrows() {
        ChatRequest req = new ChatRequest("Hi", null);
        when(userService.getCurrentUser()).thenReturn(user);
        when(chatSessionService.getOrCreate(req, user)).thenReturn(chatSession);
        when(aiService.sendMessage("Hi")).thenThrow(new RuntimeException("AI down"));

        assertThatThrownBy(() -> chatService.sendMessage(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("AI down");
    }
}
