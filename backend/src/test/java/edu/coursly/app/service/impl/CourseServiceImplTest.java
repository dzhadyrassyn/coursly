package edu.coursly.app.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.coursly.app.dto.CourseResponse;
import edu.coursly.app.mapper.CourseMapper;
import edu.coursly.app.model.entity.Course;
import edu.coursly.app.repository.CourseRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CourseServiceImplTest {

    @Mock private CourseRepository courseRepository;

    @Mock private CourseMapper courseMapper;

    @InjectMocks private CourseServiceImpl courseService;

    private Course course1;
    private Course course2;
    private CourseResponse dto1;
    private CourseResponse dto2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        course1 =
                Course.builder()
                        .id(1L)
                        .title("Spring Boot Masterclass")
                        .description("Learn backend development")
                        .build();

        course2 =
                Course.builder()
                        .id(2L)
                        .title("React for Beginners")
                        .description("Learn frontend development")
                        .build();

        dto1 = new CourseResponse(1L, "Spring Boot Masterclass", "Learn backend development");
        dto2 = new CourseResponse(2L, "React for Beginners", "Learn frontend development");
    }

    @Test
    @DisplayName("getCourses() should return mapped course list")
    void getCourses_success() {
        when(courseRepository.findAll()).thenReturn(List.of(course1, course2));
        when(courseMapper.toDto(course1)).thenReturn(dto1);
        when(courseMapper.toDto(course2)).thenReturn(dto2);

        List<CourseResponse> result = courseService.getCourses();

        assertThat(result).isNotNull().hasSize(2).containsExactly(dto1, dto2);

        verify(courseRepository).findAll();
        verify(courseMapper).toDto(course1);
        verify(courseMapper).toDto(course2);
        verifyNoMoreInteractions(courseRepository, courseMapper);
    }

    @Test
    @DisplayName("getCourses() should return empty list when no courses found")
    void getCourses_emptyList() {
        when(courseRepository.findAll()).thenReturn(List.of());

        List<CourseResponse> result = courseService.getCourses();

        assertThat(result).isNotNull().isEmpty();

        verify(courseRepository).findAll();
        verifyNoInteractions(courseMapper);
    }

    @Test
    @DisplayName("getCourse() should return a course when found")
    void getCourse_found() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course1));

        Optional<Course> result = courseService.getCourse(1L);

        assertThat(result).isPresent().contains(course1);

        verify(courseRepository).findById(1L);
        verifyNoMoreInteractions(courseRepository);
        verifyNoInteractions(courseMapper);
    }

    @Test
    @DisplayName("getCourse() should return empty optional when not found")
    void getCourse_notFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Course> result = courseService.getCourse(1L);

        assertThat(result).isNotNull().isEmpty();

        verify(courseRepository).findById(1L);
        verifyNoMoreInteractions(courseRepository);
        verifyNoInteractions(courseMapper);
    }
}
