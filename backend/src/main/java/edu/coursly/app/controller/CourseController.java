package edu.coursly.app.controller;

import edu.coursly.app.dto.CourseResponse;
import edu.coursly.app.service.CourseService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping(ApiPaths.API_V1_COURSES)
    public List<CourseResponse> getCourses() {
        return courseService.getCourses();
    }
}
