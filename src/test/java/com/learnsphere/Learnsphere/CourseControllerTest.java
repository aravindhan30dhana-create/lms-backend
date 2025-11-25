package com.learnsphere.Learnsphere;

import com.learnsphere.Learnsphere.controller.CourseController;
import com.learnsphere.Learnsphere.dto.CourseDTO;
import com.learnsphere.Learnsphere.dto.LessonDTO;
import com.learnsphere.Learnsphere.entity.Course;
import com.learnsphere.Learnsphere.security.JwtAuthenticationFilter;
import com.learnsphere.Learnsphere.security.JwtTokenProvider;
import com.learnsphere.Learnsphere.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    // JWT mocks to satisfy security
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void testGetAllCourses() throws Exception {
        CourseDTO course = CourseDTO.builder()
                .id("c1")
                .title("Java Basics")
                .category("Programming")
                .level(Course.CourseLevel.Beginner)
                .price(BigDecimal.ZERO)
                .build();

        when(courseService.getApprovedCourses()).thenReturn(List.of(course));

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("c1"))
                .andExpect(jsonPath("$[0].title").value("Java Basics"));
    }

    @Test
    void testGetCourseById() throws Exception {
        CourseDTO course = CourseDTO.builder()
                .id("c2")
                .title("Spring Boot")
                .category("Programming")
                .level(Course.CourseLevel.Intermediate)
                .price(BigDecimal.valueOf(50))
                .build();

        when(courseService.getCourseById("c2")).thenReturn(course);

        mockMvc.perform(get("/courses/c2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c2"))
                .andExpect(jsonPath("$.title").value("Spring Boot"));
    }

    @Test
    void testSearchCoursesByQuery() throws Exception {
        CourseDTO course1 = CourseDTO.builder()
                .id("c3")
                .title("React JS")
                .category("Frontend")
                .level(Course.CourseLevel.Beginner)
                .price(BigDecimal.ZERO)
                .build();

        when(courseService.searchCourses("React", null, null)).thenReturn(List.of(course1));

        mockMvc.perform(get("/courses/search")
                        .param("query", "React"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("c3"))
                .andExpect(jsonPath("$[0].title").value("React JS"));
    }

    @Test
    void testSearchCoursesByCategoryAndLevel() throws Exception {
        CourseDTO course2 = CourseDTO.builder()
                .id("c4")
                .title("Advanced Java")
                .category("Programming")
                .level(Course.CourseLevel.Advanced)
                .price(BigDecimal.valueOf(100))
                .build();

        when(courseService.searchCourses(null, "Programming", "ADVANCED")).thenReturn(List.of(course2));

        mockMvc.perform(get("/courses/search")
                        .param("category", "Programming")
                        .param("level", "ADVANCED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("c4"))
                .andExpect(jsonPath("$[0].title").value("Advanced Java"));
    }
}
