package edu.coursly.app.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import edu.coursly.app.dto.ChatRequest;
import edu.coursly.app.model.ChatSession;
import edu.coursly.app.model.User;
import edu.coursly.app.repository.ChatSessionRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class ChatSessionServiceImplTest {

    @Mock private ChatSessionRepository chatSessionRepository;

    @InjectMocks private ChatSessionServiceImpl chatSessionService;

    private User user;
    private ChatSession chatSession;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("daniyar").build();
        chatSession = ChatSession.builder().id(100L).title("Existing Session").user(user).build();
    }

    @Test
    @DisplayName("Should return existing chat session if owned by the user")
    void getOrCreate_existingOwnedSession() {
        // given
        ChatRequest request = new ChatRequest("Hello again!", chatSession.getId());
        when(chatSessionRepository.findById(100L)).thenReturn(Optional.of(chatSession));

        // when
        ChatSession result = chatSessionService.getOrCreate(request, user);

        // then
        assertThat(result).isEqualTo(chatSession);
        verify(chatSessionRepository, never()).save(any(ChatSession.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException if chat session belongs to another user")
    void getOrCreate_existingButDifferentUser() {
        // given
        User anotherUser = User.builder().id(2L).username("other").build();
        ChatSession otherSession =
                ChatSession.builder().id(200L).title("Other session").user(anotherUser).build();

        ChatRequest request = new ChatRequest("Trying to access others session", 200L);
        when(chatSessionRepository.findById(200L)).thenReturn(Optional.of(otherSession));

        // when + then
        assertThatThrownBy(() -> chatSessionService.getOrCreate(request, user))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Access denied");

        verify(chatSessionRepository, never()).save(any(ChatSession.class));
    }

    @Test
    @DisplayName("Should create new chat session if sessionId is null")
    void getOrCreate_createsNewSession() {
        // given
        ChatRequest request = new ChatRequest("Start new conversation", null);
        ChatSession newSession =
                ChatSession.builder().id(300L).title(request.message()).user(user).build();

        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(newSession);

        // when
        ChatSession result = chatSessionService.getOrCreate(request, user);

        // then
        assertThat(result).isEqualTo(newSession);
        assertThat(result.getTitle()).isEqualTo("Start new conversation");
        verify(chatSessionRepository)
                .save(
                        argThat(
                                cs ->
                                        Objects.equals(cs.getUser(), user)
                                                && cs.getTitle().equals("Start new conversation")));
    }

    @Test
    @DisplayName("Should create new chat session if no session found by ID")
    void getOrCreate_noSessionFoundById() {
        // given
        ChatRequest request = new ChatRequest("New topic", 404L);
        when(chatSessionRepository.findById(404L)).thenReturn(Optional.empty());

        ChatSession saved = ChatSession.builder().id(500L).title("New topic").user(user).build();

        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(saved);

        // when
        ChatSession result = chatSessionService.getOrCreate(request, user);

        // then
        assertThat(result.getId()).isEqualTo(500L);
        assertThat(result.getTitle()).isEqualTo("New topic");
        verify(chatSessionRepository).save(any(ChatSession.class));
    }

    @Test
    @DisplayName("Should throw NullPointerException if ChatRequest is null")
    void getOrCreate_nullChatRequest() {
        assertThatThrownBy(() -> chatSessionService.getOrCreate(null, user))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("ChatRequest must not be null");
    }

    @Test
    @DisplayName("Should throw NullPointerException if User is null")
    void getOrCreate_nullUser() {
        ChatRequest request = new ChatRequest("Hello", null);
        assertThatThrownBy(() -> chatSessionService.getOrCreate(request, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("User must not be null");
    }

    @Test
    @DisplayName("Should return all sessions for user")
    void retrieveUserChatSessions_success() {
        // given
        when(chatSessionRepository.findAllByUser(user)).thenReturn(List.of(chatSession));

        // when
        List<ChatSession> sessions = chatSessionService.retrieveUserChatSessions(user);

        // then
        assertThat(sessions).hasSize(1);
        assertThat(sessions.getFirst().getTitle()).isEqualTo("Existing Session");
        verify(chatSessionRepository).findAllByUser(user);
    }

    @Test
    @DisplayName("Should throw NullPointerException when user is null in retrieveUserChatSessions")
    void retrieveUserChatSessions_nullUser() {
        assertThatThrownBy(() -> chatSessionService.retrieveUserChatSessions(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("User must not be null");
    }
}
