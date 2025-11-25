package com.learnsphere.Learnsphere;

import com.learnsphere.Learnsphere.dto.AuthRequest;
import com.learnsphere.Learnsphere.dto.AuthResponse;
import com.learnsphere.Learnsphere.dto.SignupRequest;
import com.learnsphere.Learnsphere.entity.User;
import com.learnsphere.Learnsphere.exception.ResourceAlreadyExistsException;
import com.learnsphere.Learnsphere.repository.UserRepository;
import com.learnsphere.Learnsphere.security.JwtTokenProvider;
import com.learnsphere.Learnsphere.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private AuthService authService;

    @BeforeEach
    void init() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authenticationManager = mock(AuthenticationManager.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);

        authService = new AuthService(
                userRepository,
                passwordEncoder,
                authenticationManager,
                jwtTokenProvider
        );
    }

    @Test
    void testSignupSuccess() {
        SignupRequest req = new SignupRequest(
                "John Doe",
                "john@example.com",
                "password",
                User.UserRole.student
        );

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedpwd");

        User savedUser = User.builder()
                .id("u1")
                .name("John Doe")
                .email("john@example.com")
                .role(User.UserRole.student)
                .password("encodedpwd")
                .avatar("avatar")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtTokenProvider.generateToken(any())).thenReturn("fake-jwt-token");

        AuthResponse response = authService.signup(req);

        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("john@example.com", response.getUser().getEmail());
        assertEquals(User.UserRole.student, response.getUser().getRole());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encodedpwd", userCaptor.getValue().getPassword());
    }

    @Test
    void testSignupEmailAlreadyExists() {
        SignupRequest req = new SignupRequest(
                "John",
                "john@example.com",
                "password",
                User.UserRole.student
        );

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> authService.signup(req));
    }

    @Test
    void testLoginSuccess() {
        AuthRequest req = new AuthRequest("john@example.com", "password");

        User user = User.builder()
                .id("u1")
                .email("john@example.com")
                .password("encodedpwd")
                .role(User.UserRole.student)
                .build();

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(new UsernamePasswordAuthenticationToken(
                "john@example.com",
                "password",
                Collections.emptyList()
        ));

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        when(jwtTokenProvider.generateToken(any()))
                .thenReturn("jwt-login-token");

        AuthResponse response = authService.login(req);

        assertEquals("jwt-login-token", response.getToken());
        assertEquals("john@example.com", response.getUser().getEmail());
    }

    @Test
    void testLoginUserNotFound() {
        AuthRequest req = new AuthRequest("unknown@example.com", "password");

        when(authenticationManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken("unknown@example.com", "password")
        );

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> authService.login(req));
    }
}
