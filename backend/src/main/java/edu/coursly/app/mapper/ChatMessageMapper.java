package edu.coursly.app.mapper;

import edu.coursly.app.dto.ChatMessageResponse;
import edu.coursly.app.model.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mappings({
        @Mapping(source = "id", target = "messageId"),
        @Mapping(source = "content", target = "message"),
    })
    ChatMessageResponse toDto(ChatMessage chatMessage);
}
