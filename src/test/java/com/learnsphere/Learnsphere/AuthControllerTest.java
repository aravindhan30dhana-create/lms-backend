package com.learnsphere.Learnsphere;

import com.learnsphere.Learnsphere.controller.AuthController;
import com.learnsphere.Learnsphere.dto.AuthRequest;
import com.learnsphere.Learnsphere.dto.AuthResponse;
import com.learnsphere.Learnsphere.dto.SignupRequest;
import com.learnsphere.Learnsphere.entity.User;
import com.learnsphere.Learnsphere.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.learnsphere.Learnsphere.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void testSignup() throws Exception {
        SignupRequest req = new SignupRequest(
                "John Doe",
                "john@example.com",
                "123456",
                User.UserRole.student
        );

        AuthResponse res = AuthResponse.builder()
                .token("fake-jwt-token")
                .user(AuthResponse.UserDTO.builder()
                        .id("u1")
                        .name("John Doe")
                        .email("john@example.com")
                        .role(User.UserRole.student)
                        .avatar("avatar.png")
                        .build())
                .build();

        when(authService.signup(any(SignupRequest.class))).thenReturn(res);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.user.email").value("john@example.com"));
    }

    @Test
    void testLogin() throws Exception {
        AuthRequest req = new AuthRequest("john@example.com", "123456");

        AuthResponse res = AuthResponse.builder()
                .token("jwt-login-token")
                .user(AuthResponse.UserDTO.builder()
                        .id("u1")
                        .email("john@example.com")
                        .role(User.UserRole.student)
                        .avatar("avatar.png")
                        .name("John Doe")
                        .build())
                .build();

        when(authService.login(any(AuthRequest.class))).thenReturn(res);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-login-token"))
                .andExpect(jsonPath("$.user.email").value("john@example.com"));
    }
}
