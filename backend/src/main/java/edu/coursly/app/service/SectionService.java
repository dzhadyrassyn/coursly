package edu.coursly.app.service;

import edu.coursly.app.dto.SectionResponse;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

public interface SectionService {

    List<SectionResponse> getCourseSections(@PathVariable Long courseId);
}
