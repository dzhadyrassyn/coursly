package edu.coursly.app.mapper;

import edu.coursly.app.dto.ChatMessageResponse;
import edu.coursly.app.model.ChatMessage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    ChatMessageResponse toDto(ChatMessage chatMessage);
}
