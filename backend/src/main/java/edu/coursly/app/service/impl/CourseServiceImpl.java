package edu.coursly.app.service.impl;

import edu.coursly.app.dto.CourseResponse;
import edu.coursly.app.mapper.CourseMapper;
import edu.coursly.app.model.entity.Course;
import edu.coursly.app.repository.CourseRepository;
import edu.coursly.app.service.CourseService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseServiceImpl(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    @Override
    public List<CourseResponse> getCourses() {

        return courseRepository.findAll().stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Course> getCourse(Long courseId) {

        return courseRepository.findById(courseId);
    }
}
