package edu.coursly.app.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.coursly.app.model.dto.ChatContent;
import edu.coursly.app.model.dto.TextPart;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class ChatContentJsonMapper {

    private final ObjectMapper objectMapper;

    public ChatContentJsonMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public String toJson(ChatContent content) {
        try {
            return objectMapper.writeValueAsString(content);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize ChatContent", e);
        }
    }

    public ChatContent fromJson(String json) {
        try {
            return objectMapper.readValue(json, ChatContent.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize ChatContent", e);
        }
    }

    @Named("jsonToText")
    public String jsonToText(String json) {
        if (json == null) return null;

        ChatContent content = fromJson(json);

        return content.parts().stream()
                .filter(Objects::nonNull)
                .map(p -> ((TextPart) p).text())
                .collect(Collectors.joining("\n"));
    }
}
