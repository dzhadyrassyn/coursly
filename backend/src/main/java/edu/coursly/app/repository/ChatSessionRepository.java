package edu.coursly.app.repository;

import edu.coursly.app.model.ChatSession;
import edu.coursly.app.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    List<ChatSession> findAllByUser(User user);
}
