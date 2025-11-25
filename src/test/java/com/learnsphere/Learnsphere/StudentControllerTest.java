package com.learnsphere.Learnsphere;

import com.learnsphere.Learnsphere.controller.StudentController;
import com.learnsphere.Learnsphere.dto.EnrollmentDTO;
import com.learnsphere.Learnsphere.security.JwtAuthenticationFilter;
import com.learnsphere.Learnsphere.security.JwtTokenProvider;
import com.learnsphere.Learnsphere.service.EnrollmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EnrollmentService enrollmentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(username = "student@example.com", roles = {"STUDENT"})
    void testGetEnrollments() throws Exception {
        EnrollmentDTO dto = EnrollmentDTO.builder()
                .id("e1")
                .courseId("c1")
                .studentId("s1")
                .progress(50)
                .enrolledAt(LocalDateTime.now())
                .build();

        when(enrollmentService.getStudentEnrollments("student@example.com"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/student/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("e1"))
                .andExpect(jsonPath("$[0].courseId").value("c1"));
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = {"STUDENT"})
    void testEnrollInCourse() throws Exception {
        EnrollmentDTO dto = EnrollmentDTO.builder()
                .id("e2")
                .courseId("c1")
                .studentId("s1")
                .progress(0)
                .build();

        when(enrollmentService.enrollInCourse("c1", "student@example.com"))
                .thenReturn(dto);

        mockMvc.perform(post("/student/enroll/c1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("e2"));
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = {"STUDENT"})
    void testGetEnrollment() throws Exception {
        EnrollmentDTO dto = EnrollmentDTO.builder()
                .id("e3")
                .courseId("c99")
                .progress(30)
                .build();

        when(enrollmentService.getEnrollment("c99", "student@example.com"))
                .thenReturn(dto);

        mockMvc.perform(get("/student/enrollments/c99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("e3"));
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = {"STUDENT"})
    void testCompleteLesson() throws Exception {
        Map<String, String> request = Map.of("lessonId", "l22");

        EnrollmentDTO dto = EnrollmentDTO.builder()
                .id("e5")
                .completedLessons(List.of("l22"))
                .progress(20)
                .build();

        when(enrollmentService.updateProgress("e5", "l22", "student@example.com"))
                .thenReturn(dto);

        mockMvc.perform(put("/student/enrollments/e5/complete-lesson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completedLessons[0]").value("l22"));
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = {"STUDENT"})
    void testUncompleteLesson() throws Exception {
        Map<String, String> request = Map.of("lessonId", "l22");

        EnrollmentDTO dto = EnrollmentDTO.builder()
                .id("e5")
                .completedLessons(List.of())
                .progress(0)
                .build();

        when(enrollmentService.removeCompletedLesson("e5", "l22", "student@example.com"))
                .thenReturn(dto);

        mockMvc.perform(put("/student/enrollments/e5/uncomplete-lesson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completedLessons").isEmpty());
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = {"STUDENT"})
    void testUnenroll() throws Exception {
        doNothing().when(enrollmentService).unenroll("e44", "student@example.com");

        mockMvc.perform(delete("/student/enrollments/e44"))
                .andExpect(status().isNoContent());
    }
}
