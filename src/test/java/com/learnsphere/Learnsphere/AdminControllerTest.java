package com.learnsphere.Learnsphere;

import com.learnsphere.Learnsphere.controller.AdminController;
import com.learnsphere.Learnsphere.dto.AuthResponse;
import com.learnsphere.Learnsphere.dto.CourseDTO;
import com.learnsphere.Learnsphere.security.JwtAuthenticationFilter;
import com.learnsphere.Learnsphere.security.JwtTokenProvider;
import com.learnsphere.Learnsphere.service.CourseService;
import com.learnsphere.Learnsphere.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CourseService courseService;

    // ADD THESE TWO MOCKS
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void testGetAllUsers() throws Exception {
        AuthResponse.UserDTO user1 = AuthResponse.UserDTO.builder()
                .id("u1").name("Alice").email("alice@example.com").build();

        AuthResponse.UserDTO user2 = AuthResponse.UserDTO.builder()
                .id("u2").name("Bob").email("bob@example.com").build();

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("alice@example.com"))
                .andExpect(jsonPath("$[1].email").value("bob@example.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser("u123");

        mockMvc.perform(delete("/admin/users/u123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllCourses() throws Exception {
        CourseDTO c1 = CourseDTO.builder()
                .id("c1")
                .title("Course One")
                .category("cat")
                .price(BigDecimal.ZERO)
                .build();

        when(courseService.getAllCourses()).thenReturn(List.of(c1));

        mockMvc.perform(get("/admin/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("c1"));
    }

    @Test
    void testApproveCourse() throws Exception {
        CourseDTO approved = CourseDTO.builder().id("c1").title("Approved").build();

        when(courseService.approveCourse("c1")).thenReturn(approved);

        mockMvc.perform(put("/admin/courses/c1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c1"));
    }

    @Test
    void testDisapproveCourse() throws Exception {
        CourseDTO disapproved = CourseDTO.builder().id("c2").title("Disapproved").build();

        when(courseService.disapproveCourse("c2")).thenReturn(disapproved);

        mockMvc.perform(put("/admin/courses/c2/disapprove"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c2"));
    }

    @Test
    void testDeleteCourse() throws Exception {
        doNothing().when(courseService).deleteCourse("c9", null);

        mockMvc.perform(delete("/admin/courses/c9"))
                .andExpect(status().isNoContent());
    }
}
