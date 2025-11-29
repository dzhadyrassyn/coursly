package edu.coursly.app.controller;

import edu.coursly.app.dto.SectionResponse;
import edu.coursly.app.service.SectionService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping(ApiPaths.API_V1_COURSE_SECTIONS)
    public List<SectionResponse> getCourseSections(@PathVariable Long courseId) {
        return sectionService.getCourseSections(courseId);
    }
}
