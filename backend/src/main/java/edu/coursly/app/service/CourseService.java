package edu.coursly.app.service;

import edu.coursly.app.dto.CourseResponse;
import edu.coursly.app.model.entity.Course;
import java.util.List;
import java.util.Optional;

public interface CourseService {

    List<CourseResponse> getCourses();

    Optional<Course> getCourse(Long courseId);
}
