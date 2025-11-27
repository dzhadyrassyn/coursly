package edu.coursly.app.service;

import edu.coursly.app.dto.CourseResponse;
import java.util.List;

public interface CourseService {

    List<CourseResponse> getCourses();
}
