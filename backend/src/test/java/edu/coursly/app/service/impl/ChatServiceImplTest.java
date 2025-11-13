package edu.coursly.app.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

import edu.coursly.app.dto.ChatMessageResponse;
import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.dto.ChatResponse;
import edu.coursly.app.dto.ChatSessionResponse;
import edu.coursly.app.mapper.ChatMessageMapper;
import edu.coursly.app.mapper.ChatSessionMapper;
import edu.coursly.app.model.ChatMessage;
import edu.coursly.app.model.ChatSession;
import edu.coursly.app.model.User;
import edu.coursly.app.model.enums.MessageSenderType;
import edu.coursly.app.service.AIService;
import edu.coursly.app.service.ChatMessageService;
import edu.coursly.app.service.ChatSessionService;
import edu.coursly.app.service.UserService;
import java.time.Instant;
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
    @Mock private ChatMessageMapper chatMessageMapper;

    @InjectMocks private ChatServiceImpl chatService;

    private User user;
    private ChatSession chatSession;
    private ChatMessage message1;
    private ChatMessage message2;
    private ChatMessageResponse dto1;
    private ChatMessageResponse dto2;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("daniyar").build();
        chatSession = ChatSession.builder().id(42L).title("Session 1").user(user).build();

        message1 =
                ChatMessage.builder()
                        .id(100L)
                        .content("Hello!")
                        .sender(MessageSenderType.USER)
                        .chatSession(chatSession)
                        .created(Instant.parse("2025-11-03T10:00:00Z"))
                        .lastModified(Instant.parse("2025-11-03T10:00:01Z"))
                        .build();

        message2 =
                ChatMessage.builder()
                        .id(101L)
                        .content("Hi, how can I help?")
                        .sender(MessageSenderType.AI)
                        .chatSession(chatSession)
                        .created(Instant.parse("2025-11-03T10:00:02Z"))
                        .lastModified(Instant.parse("2025-11-03T10:00:03Z"))
                        .build();

        dto1 =
                new ChatMessageResponse(
                        100L, "Hello!", "USER", message1.getCreated(), message1.getLastModified());
        dto2 =
                new ChatMessageResponse(
                        101L,
                        "Hi, how can I help?",
                        "AI",
                        message2.getCreated(),
                        message2.getLastModified());
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

    @Test
    @DisplayName("Should return mapped ChatMessageResponses for user and session")
    void retrieveUserChatSessionMessages_success() {
        // given
        when(userService.getCurrentUser()).thenReturn(user);
        when(chatMessageService.retrieveMessages(42L, user))
                .thenReturn(List.of(message1, message2));
        when(chatMessageMapper.toDto(message1)).thenReturn(dto1);
        when(chatMessageMapper.toDto(message2)).thenReturn(dto2);

        // when
        List<ChatMessageResponse> result = chatService.retrieveUserChatSessionMessages(42L);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).messageId()).isEqualTo(100L);
        assertThat(result.get(0).message()).isEqualTo("Hello!");
        assertThat(result.get(1).sender()).isEqualTo("AI");

        verify(userService).getCurrentUser();
        verify(chatMessageService).retrieveMessages(42L, user);
        verify(chatMessageMapper).toDto(message1);
        verify(chatMessageMapper).toDto(message2);
    }

    @Test
    @DisplayName("Should return empty list when no messages found")
    void retrieveUserChatSessionMessages_empty() {
        // given
        when(userService.getCurrentUser()).thenReturn(user);
        when(chatMessageService.retrieveMessages(42L, user)).thenReturn(List.of());

        // when
        List<ChatMessageResponse> result = chatService.retrieveUserChatSessionMessages(42L);

        // then
        assertThat(result.isEmpty()).isTrue();
        verify(chatMessageService).retrieveMessages(42L, user);
        verifyNoInteractions(chatMessageMapper);
    }

    @Test
    @DisplayName("Should throw NullPointerException when chatSessionId is null")
    void retrieveUserChatSessionMessages_nullId() {
        // when + then
        assertThatThrownBy(() -> chatService.retrieveUserChatSessionMessages(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("chatSessionId cannot be null");

        verifyNoInteractions(chatMessageService);
    }
}
