package edu.coursly.app.repository;

import edu.coursly.app.model.entity.ChatSession;
import edu.coursly.app.model.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    List<ChatSession> findAllByUser(User user);
}
