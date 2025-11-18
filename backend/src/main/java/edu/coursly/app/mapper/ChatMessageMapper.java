package edu.coursly.app.mapper;

import edu.coursly.app.dto.ChatMessageResponse;
import edu.coursly.app.model.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        componentModel = "spring",
        uses = {ChatContentJsonMapper.class})
public interface ChatMessageMapper {

    @Mappings({
        @Mapping(source = "id", target = "messageId"),
        @Mapping(source = "contentJson", target = "message", qualifiedByName = "jsonToText"),
    })
    ChatMessageResponse toDto(ChatMessage chatMessage);
}
