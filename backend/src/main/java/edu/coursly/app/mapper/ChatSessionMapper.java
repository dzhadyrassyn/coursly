package edu.coursly.app.mapper;

import edu.coursly.app.dto.ChatSessionResponse;
import edu.coursly.app.model.entity.ChatSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatSessionMapper {

    @Mapping(source = "id", target = "chatSessionId")
    ChatSessionResponse toDto(ChatSession chatSession);
}
