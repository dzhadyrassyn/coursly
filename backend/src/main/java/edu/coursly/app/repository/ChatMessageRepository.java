package edu.coursly.app.repository;

import edu.coursly.app.model.ChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByChatSession_IdOrderByCreatedAsc(Long chatSessionId);
}
