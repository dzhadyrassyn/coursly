package edu.coursly.app.mapper;

import edu.coursly.app.dto.SectionResponse;
import edu.coursly.app.model.entity.Section;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SectionMapper {

    SectionResponse toDto(Section section);
}
