package edu.coursly.app.mapper;

import edu.coursly.app.dto.ChatSessionResponse;
import edu.coursly.app.model.ChatSession;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatSessionMapper {

    ChatSessionResponse toDto(ChatSession chatSession);
}
