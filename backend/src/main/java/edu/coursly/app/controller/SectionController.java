package edu.coursly.app.controller;

import edu.coursly.app.dto.SectionResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SectionController {

    @GetMapping(ApiPaths.API_V1_COURSE_SECTIONS)
    public List<SectionResponse> getCourseSections(@PathVariable Long courseId) {
        return List.of(); // TODO Add implementation
    }
}
