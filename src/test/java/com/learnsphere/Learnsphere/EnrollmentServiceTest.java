package com.learnsphere.Learnsphere;

import com.learnsphere.Learnsphere.dto.EnrollmentDTO;
import com.learnsphere.Learnsphere.entity.Course;
import com.learnsphere.Learnsphere.entity.Enrollment;
import com.learnsphere.Learnsphere.entity.User;
import com.learnsphere.Learnsphere.repository.CourseRepository;
import com.learnsphere.Learnsphere.repository.EnrollmentRepository;
import com.learnsphere.Learnsphere.repository.LessonRepository;
import com.learnsphere.Learnsphere.repository.UserRepository;
import com.learnsphere.Learnsphere.service.CourseService;
import com.learnsphere.Learnsphere.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EnrollmentServiceTest {

    private EnrollmentRepository enrollmentRepository;
    private CourseRepository courseRepository;
    private UserRepository userRepository;
    private LessonRepository lessonRepository;
    private CourseService courseService;
    private EnrollmentService enrollmentService;

    User student;
    Course course;

    @BeforeEach
    void setUp() {
        enrollmentRepository = mock(EnrollmentRepository.class);
        courseRepository = mock(CourseRepository.class);
        userRepository = mock(UserRepository.class);
        lessonRepository = mock(LessonRepository.class);
        courseService = mock(CourseService.class);

        enrollmentService = new EnrollmentService(
                enrollmentRepository,
                courseRepository,
                userRepository,
                lessonRepository,
                courseService
        );

        student = User.builder()
                .id("s1")
                .email("student@example.com")
                .role(User.UserRole.student)
                .build();

        course = Course.builder()
                .id("c1")
                .isApproved(true)
                .enrolledStudents(0)
                .build();
    }

    @Test
    void testEnrollInCourseSuccess() {
        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(courseRepository.findById("c1")).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(false);
        when(enrollmentRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        EnrollmentDTO dto = enrollmentService.enrollInCourse("c1", "student@example.com");

        assertEquals("c1", dto.getCourseId());
        assertEquals("s1", dto.getStudentId());
        assertEquals(0, dto.getProgress());

        verify(courseRepository).save(course);
    }

    @Test
    void testUnenroll() {
        Enrollment enrollment = Enrollment.builder()
                .id("e1")
                .student(student)
                .course(course)
                .build();

        when(enrollmentRepository.findById("e1")).thenReturn(Optional.of(enrollment));
        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));

        enrollmentService.unenroll("e1", "student@example.com");

        verify(enrollmentRepository).delete(enrollment);
        assertEquals(0, course.getEnrolledStudents());
    }

    @Test
    void testGetEnrollment() {
        Enrollment enrollment = Enrollment.builder()
                .id("e77")
                .student(student)
                .course(course)
                .build();

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(courseRepository.findById("c1")).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByStudentAndCourse(student, course))
                .thenReturn(Optional.of(enrollment));

        EnrollmentDTO dto = enrollmentService.getEnrollment("c1", "student@example.com");

        assertEquals("e77", dto.getId());
        assertEquals("c1", dto.getCourseId());
    }
}
