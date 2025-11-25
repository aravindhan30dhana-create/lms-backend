package com.learnsphere.Learnsphere;

import com.learnsphere.Learnsphere.controller.InstructorController;
import com.learnsphere.Learnsphere.dto.CourseDTO;
import com.learnsphere.Learnsphere.entity.Course;
import com.learnsphere.Learnsphere.security.JwtAuthenticationFilter;
import com.learnsphere.Learnsphere.security.JwtTokenProvider;
import com.learnsphere.Learnsphere.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstructorController.class)
@AutoConfigureMockMvc(addFilters = false)
public class InstructorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(username = "instructor@example.com", roles = {"INSTRUCTOR"})
    void testGetInstructorCourses() throws Exception {
        CourseDTO course = CourseDTO.builder()
                .id("c1")
                .title("Java Basics")
                .category("Programming")
                .level(Course.CourseLevel.Beginner)
                .price(BigDecimal.ZERO)
                .build();

        when(courseService.getCoursesByInstructor("instructor@example.com"))
                .thenReturn(List.of(course));

        mockMvc.perform(get("/instructor/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("c1"))
                .andExpect(jsonPath("$[0].title").value("Java Basics"));
    }

    @Test
    @WithMockUser(username = "instructor@example.com", roles = {"INSTRUCTOR"})
    void testCreateCourse() throws Exception {
        CourseDTO courseDTO = CourseDTO.builder()
                .title("Spring Boot")
                .category("Programming")
                .level(Course.CourseLevel.Intermediate)
                .price(BigDecimal.valueOf(50))
                .build();

        CourseDTO createdCourse = CourseDTO.builder()
                .id("c2")
                .title("Spring Boot")
                .category("Programming")
                .level(Course.CourseLevel.Intermediate)
                .price(BigDecimal.valueOf(50))
                .build();

        when(courseService.createCourse(any(CourseDTO.class), eq("instructor@example.com")))
                .thenReturn(createdCourse);

        mockMvc.perform(post("/instructor/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("c2"))
                .andExpect(jsonPath("$.title").value("Spring Boot"));
    }

    @Test
    @WithMockUser(username = "instructor@example.com", roles = {"INSTRUCTOR"})
    void testUpdateCourse() throws Exception {
        CourseDTO updateDTO = CourseDTO.builder()
                .title("Spring Boot Advanced")
                .category("Programming")
                .level(Course.CourseLevel.Advanced)
                .price(BigDecimal.valueOf(100))
                .build();

        CourseDTO updatedCourse = CourseDTO.builder()
                .id("c2")
                .title("Spring Boot Advanced")
                .category("Programming")
                .level(Course.CourseLevel.Advanced)
                .price(BigDecimal.valueOf(100))
                .build();

        when(courseService.updateCourse(eq("c2"), any(CourseDTO.class), eq("instructor@example.com")))
                .thenReturn(updatedCourse);

        mockMvc.perform(put("/instructor/courses/c2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c2"))
                .andExpect(jsonPath("$.title").value("Spring Boot Advanced"));
    }

    @Test
    @WithMockUser(username = "instructor@example.com", roles = {"INSTRUCTOR"})
    void testDeleteCourse() throws Exception {
        doNothing().when(courseService).deleteCourse("c3", "instructor@example.com");

        mockMvc.perform(delete("/instructor/courses/c3"))
                .andExpect(status().isNoContent());
    }
}
