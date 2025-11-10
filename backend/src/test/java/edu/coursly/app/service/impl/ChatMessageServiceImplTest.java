package edu.coursly.app.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.coursly.app.model.ChatMessage;
import edu.coursly.app.model.ChatSession;
import edu.coursly.app.model.User;
import edu.coursly.app.model.enums.MessageSenderType;
import edu.coursly.app.repository.ChatMessageRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

    @Mock private ChatMessageRepository chatMessageRepository;

    @InjectMocks private ChatMessageServiceImpl chatMessageService;

    private ChatSession chatSession;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("daniyar").build();
        chatSession = ChatSession.builder().id(42L).title("Session").user(user).build();
    }

    @Test
    @DisplayName("Should save user message with correct content and sender")
    void saveUserMessage_success() {
        // when
        chatMessageService.saveUserMessage("Hello AI!", chatSession);

        // then
        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(captor.capture());

        ChatMessage saved = captor.getValue();
        assertThat(saved.getContent()).isEqualTo("Hello AI!");
        assertThat(saved.getChatSession()).isEqualTo(chatSession);
        assertThat(saved.getSender()).isEqualTo(MessageSenderType.USER);
    }

    @Test
    @DisplayName("Should save AI message with correct content and sender")
    void saveAIMessage_success() {
        // when
        chatMessageService.saveAIMessage("Hi human!", chatSession);

        // then
        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(captor.capture());

        ChatMessage saved = captor.getValue();
        assertThat(saved.getContent()).isEqualTo("Hi human!");
        assertThat(saved.getChatSession()).isEqualTo(chatSession);
        assertThat(saved.getSender()).isEqualTo(MessageSenderType.AI);
    }

    @Test
    @DisplayName("Should return all chat messages when user owns the session")
    void retrieveMessages_success() {
        // given
        ChatMessage m1 = ChatMessage.builder().id(1L).chatSession(chatSession).build();
        ChatMessage m2 = ChatMessage.builder().id(2L).chatSession(chatSession).build();
        when(chatMessageRepository.findAllByChatSession_IdOrderByCreatedAsc(42L))
                .thenReturn(List.of(m1, m2));

        // when
        List<ChatMessage> result = chatMessageService.retrieveMessages(42L, user);

        // then
        assertThat(result).hasSize(2);
        verify(chatMessageRepository).findAllByChatSession_IdOrderByCreatedAsc(42L);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException if user does not own session")
    void retrieveMessages_accessDenied() {
        // given
        User anotherUser = User.builder().id(99L).username("other").build();
        ChatSession otherSession = ChatSession.builder().id(50L).user(anotherUser).build();

        ChatMessage m1 = ChatMessage.builder().id(1L).chatSession(otherSession).build();
        when(chatMessageRepository.findAllByChatSession_IdOrderByCreatedAsc(50L))
                .thenReturn(List.of(m1));

        // when + then
        assertThatThrownBy(() -> chatMessageService.retrieveMessages(50L, user))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("access to this chat session");

        verify(chatMessageRepository).findAllByChatSession_IdOrderByCreatedAsc(50L);
    }

    @Test
    @DisplayName("Should return empty list if no messages found")
    void retrieveMessages_emptyList() {
        // given
        when(chatMessageRepository.findAllByChatSession_IdOrderByCreatedAsc(42L))
                .thenReturn(List.of());

        // when
        List<ChatMessage> result = chatMessageService.retrieveMessages(42L, user);

        // then
        assertThat(result).isEmpty();
        verify(chatMessageRepository).findAllByChatSession_IdOrderByCreatedAsc(42L);
    }
}
