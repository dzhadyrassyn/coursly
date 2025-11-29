package edu.coursly.app.service.impl;

import edu.coursly.app.dto.SectionResponse;
import edu.coursly.app.mapper.SectionMapper;
import edu.coursly.app.repository.SectionRepository;
import edu.coursly.app.service.CourseService;
import edu.coursly.app.service.SectionService;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final SectionMapper sectionMapper;
    private final CourseService courseService;

    public SectionServiceImpl(
            SectionRepository sectionRepository,
            SectionMapper sectionMapper,
            CourseService courseService) {
        this.sectionRepository = sectionRepository;
        this.sectionMapper = sectionMapper;
        this.courseService = courseService;
    }

    @Override
    public List<SectionResponse> getCourseSections(Long courseId) {

        Objects.requireNonNull(courseId, "Course ID cannot be null");

        courseService
                .getCourse(courseId)
                .orElseThrow(
                        () -> new RuntimeException("Course is not found. Course ID: " + courseId));

        return sectionRepository.findAllByCourse_Id(courseId).stream()
                .map(sectionMapper::toDto)
                .toList();
    }
}
