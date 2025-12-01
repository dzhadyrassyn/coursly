package edu.coursly.app.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.coursly.app.dto.SectionResponse;
import edu.coursly.app.mapper.SectionMapper;
import edu.coursly.app.model.entity.Course;
import edu.coursly.app.model.entity.Section;
import edu.coursly.app.repository.SectionRepository;
import edu.coursly.app.service.CourseService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SectionServiceImplTest {

    @Mock private SectionRepository sectionRepository;

    @Mock private SectionMapper sectionMapper;

    @Mock private CourseService courseService;

    @InjectMocks private SectionServiceImpl sectionService;

    private Course course;
    private Section section1;
    private Section section2;
    private SectionResponse dto1;
    private SectionResponse dto2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        course =
                Course.builder()
                        .id(10L)
                        .title("Java Masterclass")
                        .description("A complete Java course")
                        .build();

        section1 = Section.builder().id(1L).content("Introduction").course(course).build();

        section2 = Section.builder().id(2L).content("OOP Basics").course(course).build();

        dto1 = new SectionResponse(1L, "Introduction");
        dto2 = new SectionResponse(2L, "OOP Basics");
    }

    @Test
    @DisplayName("Should throw NullPointerException when courseId is null")
    void getCourseSections_nullCourseId() {
        assertThatThrownBy(() -> sectionService.getCourseSections(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Course ID cannot be null");

        verifyNoInteractions(courseService, sectionRepository, sectionMapper);
    }

    @Test
    @DisplayName("Should throw RuntimeException when course does not exist")
    void getCourseSections_courseNotFound() {
        when(courseService.getCourse(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sectionService.getCourseSections(10L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Course is not found. Course ID: 10");

        verify(courseService).getCourse(10L);
        verifyNoMoreInteractions(courseService);
        verifyNoInteractions(sectionRepository, sectionMapper);
    }

    @Test
    @DisplayName("Should return mapped section responses for valid course")
    void getCourseSections_success() {
        when(courseService.getCourse(10L)).thenReturn(Optional.of(course));
        when(sectionRepository.findAllByCourse_Id(10L)).thenReturn(List.of(section1, section2));
        when(sectionMapper.toDto(section1)).thenReturn(dto1);
        when(sectionMapper.toDto(section2)).thenReturn(dto2);

        List<SectionResponse> result = sectionService.getCourseSections(10L);

        assertThat(result).isNotNull().hasSize(2).containsExactly(dto1, dto2);

        verify(courseService).getCourse(10L);
        verify(sectionRepository).findAllByCourse_Id(10L);
        verify(sectionMapper).toDto(section1);
        verify(sectionMapper).toDto(section2);
        verifyNoMoreInteractions(courseService, sectionRepository, sectionMapper);
    }
}
