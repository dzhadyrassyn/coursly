package edu.coursly.app.mapper;

import edu.coursly.app.dto.CourseResponse;
import edu.coursly.app.model.entity.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseResponse toDto(Course course);
}
