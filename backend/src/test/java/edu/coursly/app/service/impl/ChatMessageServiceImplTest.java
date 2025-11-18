package edu.coursly.app.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import edu.coursly.app.mapper.ChatContentJsonMapper;
import edu.coursly.app.model.dto.ChatContent;
import edu.coursly.app.model.dto.TextPart;
import edu.coursly.app.model.entity.ChatMessage;
import edu.coursly.app.model.entity.ChatSession;
import edu.coursly.app.model.entity.User;
import edu.coursly.app.model.enums.MessageSenderType;
import edu.coursly.app.repository.ChatMessageRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

    @Mock private ChatMessageRepository chatMessageRepository;

    @Mock private ChatContentJsonMapper chatContentJsonMapper;

    private ChatMessageServiceImpl chatMessageService;

    private User user;
    private ChatSession chatSession;

    @BeforeEach
    void setUp() {
        chatMessageService =
                new ChatMessageServiceImpl(chatMessageRepository, chatContentJsonMapper);

        user = User.builder().id(1L).username("daniyar").build();

        chatSession = ChatSession.builder().id(42L).title("Test session").user(user).build();
    }

    @Test
    @DisplayName("Should save USER message with correct content and sender")
    void saveUserMessage_success() {
        // given
        String content = "Hello AI!";
        when(chatContentJsonMapper.toJson(any(ChatContent.class))).thenReturn("{json}");

        // when
        chatMessageService.saveUserMessage(content, chatSession);

        // then
        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(captor.capture());
        verify(chatContentJsonMapper).toJson(any(ChatContent.class));
        verifyNoMoreInteractions(chatMessageRepository, chatContentJsonMapper);

        ChatMessage saved = captor.getValue();
        assertThat(saved.getChatSession()).isEqualTo(chatSession);
        assertThat(saved.getSender()).isEqualTo(MessageSenderType.USER);
        assertThat(saved.getContentJson()).isEqualTo("{json}");
    }

    @Test
    @DisplayName("Should save MODEL message with correct content and sender")
    void saveAIMessage_success() {
        // given
        String content = "Hi human!";
        when(chatContentJsonMapper.toJson(any(ChatContent.class))).thenReturn("{json-model}");

        // when
        chatMessageService.saveAIMessage(content, chatSession);

        // then
        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(captor.capture());
        verify(chatContentJsonMapper).toJson(any(ChatContent.class));
        verifyNoMoreInteractions(chatMessageRepository, chatContentJsonMapper);

        ChatMessage saved = captor.getValue();
        assertThat(saved.getChatSession()).isEqualTo(chatSession);
        assertThat(saved.getSender()).isEqualTo(MessageSenderType.MODEL);
        assertThat(saved.getContentJson()).isEqualTo("{json-model}");
    }

    @Test
    @DisplayName("Should return messages when user owns the session")
    void retrieveMessages_success_whenUserOwnsSession() {
        // given
        ChatMessage msg1 =
                ChatMessage.builder()
                        .id(100L)
                        .contentJson("{json-1}")
                        .sender(MessageSenderType.USER)
                        .chatSession(chatSession)
                        .created(Instant.parse("2025-11-03T10:00:00Z"))
                        .lastModified(Instant.parse("2025-11-03T10:00:01Z"))
                        .build();

        ChatMessage msg2 =
                ChatMessage.builder()
                        .id(101L)
                        .contentJson("{json-2}")
                        .sender(MessageSenderType.MODEL)
                        .chatSession(chatSession)
                        .created(Instant.parse("2025-11-03T10:00:02Z"))
                        .lastModified(Instant.parse("2025-11-03T10:00:03Z"))
                        .build();

        when(chatMessageRepository.findAllByChatSession_IdOrderByCreatedAsc(42L))
                .thenReturn(List.of(msg1, msg2));

        // when
        List<ChatMessage> result = chatMessageService.retrieveMessages(42L, user);

        // then
        assertThat(result).containsExactly(msg1, msg2);
        verify(chatMessageRepository).findAllByChatSession_IdOrderByCreatedAsc(42L);
        verifyNoMoreInteractions(chatMessageRepository);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when user does not own the session")
    void retrieveMessages_accessDenied_forDifferentUser() {
        // given: session belongs to user with id=1, but requester has id=99
        User otherUser = User.builder().id(99L).username("other").build();

        ChatSession foreignSession =
                ChatSession.builder()
                        .id(42L)
                        .title("Foreign session")
                        .user(user) // owner is user (id=1)
                        .build();

        ChatMessage msg =
                ChatMessage.builder()
                        .id(200L)
                        .contentJson("{json}")
                        .sender(MessageSenderType.USER)
                        .chatSession(foreignSession)
                        .created(Instant.parse("2025-11-03T10:00:00Z"))
                        .lastModified(Instant.parse("2025-11-03T10:00:01Z"))
                        .build();

        when(chatMessageRepository.findAllByChatSession_IdOrderByCreatedAsc(42L))
                .thenReturn(List.of(msg));

        // when + then
        assertThatThrownBy(() -> chatMessageService.retrieveMessages(42L, otherUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You do not have access to this chat session");

        verify(chatMessageRepository).findAllByChatSession_IdOrderByCreatedAsc(42L);
        verifyNoMoreInteractions(chatMessageRepository);
    }

    @Test
    @DisplayName("Should map last 10 messages from JSON to ChatContent")
    void retrieveLast10Messages_success() {
        // given
        ChatMessage msg1 =
                ChatMessage.builder()
                        .id(300L)
                        .contentJson("{json-1}")
                        .sender(MessageSenderType.USER)
                        .chatSession(chatSession)
                        .created(Instant.parse("2025-11-03T10:00:00Z"))
                        .lastModified(Instant.parse("2025-11-03T10:00:01Z"))
                        .build();

        ChatMessage msg2 =
                ChatMessage.builder()
                        .id(301L)
                        .contentJson("{json-2}")
                        .sender(MessageSenderType.MODEL)
                        .chatSession(chatSession)
                        .created(Instant.parse("2025-11-03T10:00:02Z"))
                        .lastModified(Instant.parse("2025-11-03T10:00:03Z"))
                        .build();

        when(chatMessageRepository.findTop10ByChatSession_IdOrderByCreatedAsc(42L))
                .thenReturn(List.of(msg1, msg2));

        ChatContent content1 = new ChatContent("user", List.of(new TextPart("Hello")));
        ChatContent content2 =
                new ChatContent("model", List.of(new TextPart("Hi, how can I help?")));

        when(chatContentJsonMapper.fromJson("{json-1}")).thenReturn(content1);
        when(chatContentJsonMapper.fromJson("{json-2}")).thenReturn(content2);

        // when
        List<ChatContent> result = chatMessageService.retrieveLast10Messages(42L);

        // then
        assertThat(result).containsExactly(content1, content2);

        verify(chatMessageRepository).findTop10ByChatSession_IdOrderByCreatedAsc(42L);
        verify(chatContentJsonMapper).fromJson("{json-1}");
        verify(chatContentJsonMapper).fromJson("{json-2}");
        verifyNoMoreInteractions(chatMessageRepository, chatContentJsonMapper);
    }
}
