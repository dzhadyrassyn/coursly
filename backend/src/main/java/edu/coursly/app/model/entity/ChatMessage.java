package edu.coursly.app.model.entity;

import edu.coursly.app.model.enums.MessageSenderType;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false, columnDefinition = "jsonb")
    private String contentJson;

    @Enumerated(EnumType.STRING)
    private MessageSenderType sender;

    @Column(name = "created")
    @CreationTimestamp
    private Instant created;

    @Column(name = "last_modified")
    @UpdateTimestamp
    private Instant lastModified;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_session_id", nullable = false, referencedColumnName = "id")
    private ChatSession chatSession;
}
