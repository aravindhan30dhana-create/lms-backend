package com.learnsphere.Learnsphere;

import com.learnsphere.Learnsphere.dto.CourseDTO;
import com.learnsphere.Learnsphere.dto.LessonDTO;
import com.learnsphere.Learnsphere.entity.Course;
import com.learnsphere.Learnsphere.entity.Lesson;
import com.learnsphere.Learnsphere.entity.User;
import com.learnsphere.Learnsphere.exception.ResourceNotFoundException;
import com.learnsphere.Learnsphere.repository.CourseRepository;
import com.learnsphere.Learnsphere.repository.LessonRepository;
import com.learnsphere.Learnsphere.repository.UserRepository;
import com.learnsphere.Learnsphere.service.CourseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private UserRepository userRepository;

    private CourseService courseService;

    @BeforeEach
    void setup() {
        courseService = new CourseService(courseRepository, lessonRepository, userRepository);
    }

    @Test
    void testCreateCourse_success_and_createLessons() {

        User instructor = User.builder()
                .id("instr1")
                .email("ins@example.com")
                .name("Instructor")
                .role(User.UserRole.instructor)
                .build();

        when(userRepository.findByEmail("ins@example.com")).thenReturn(Optional.of(instructor));

        LessonDTO lessonDTO = LessonDTO.builder()
                .title("L1")
                .description("d")
                .type(Lesson.ContentType.video)   // FIXED ENUM
                .url("u")
                .duration("10m")
                .order(1)
                .build();

        CourseDTO input = CourseDTO.builder()
                .title("New")
                .description("desc")
                .category("cat")
                .level(Course.CourseLevel.Beginner)
                .duration("2h")
                .price(BigDecimal.TEN)
                .lessons(List.of(lessonDTO))
                .build();

        Course saved = Course.builder()
                .id("c1")
                .title("New")
                .description("desc")
                .instructor(instructor)
                .category("cat")
                .level(Course.CourseLevel.Beginner)
                .duration("2h")
                .price(BigDecimal.TEN)
                .isApproved(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(courseRepository.save(any(Course.class))).thenReturn(saved);
        when(lessonRepository.findByCourseIdOrderByOrderIndexAsc("c1")).thenReturn(List.of());

        CourseDTO result = courseService.createCourse(input, "ins@example.com");

        assertNotNull(result);
        assertEquals("New", result.getTitle());
    }

    @Test
    void testGetAllCourses() {

        User instructor = User.builder()
                .id("i1")
                .email("i@example.com")
                .name("Inst")
                .role(User.UserRole.instructor)
                .build();

        Course c1 = Course.builder()
                .id("c1")
                .title("C1")
                .instructor(instructor)   // FIXED NPE
                .isApproved(true)
                .build();

        when(courseRepository.findAll()).thenReturn(List.of(c1));
        when(lessonRepository.findByCourseIdOrderByOrderIndexAsc("c1")).thenReturn(List.of());

        var list = courseService.getAllCourses();

        assertEquals(1, list.size());
        assertEquals("C1", list.get(0).getTitle());
    }

    @Test
    void testApproveCourse_notFound() {
        when(courseRepository.findById("bad")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> courseService.approveCourse("bad"));
    }

    @Test
    void testGetCourseById_found() {

        User instructor = User.builder()
                .id("i1")
                .email("i@example.com")
                .name("Inst")
                .role(User.UserRole.instructor)
                .build();

        Course c = Course.builder()
                .id("xyz")
                .title("title")
                .instructor(instructor)   // FIXED NPE
                .isApproved(true)
                .build();

        when(courseRepository.findById("xyz")).thenReturn(Optional.of(c));
        when(lessonRepository.findByCourseIdOrderByOrderIndexAsc("xyz")).thenReturn(List.of());

        CourseDTO dto = courseService.getCourseById("xyz");

        assertEquals("title", dto.getTitle());
    }
}
