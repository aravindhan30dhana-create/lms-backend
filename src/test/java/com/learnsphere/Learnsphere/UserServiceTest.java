package com.learnsphere.Learnsphere;


import com.learnsphere.Learnsphere.dto.AuthResponse;
import com.learnsphere.Learnsphere.entity.User;
import com.learnsphere.Learnsphere.exception.ResourceNotFoundException;
import com.learnsphere.Learnsphere.repository.UserRepository;
import com.learnsphere.Learnsphere.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService using Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setup() {
        userService = new UserService(userRepository);
    }

    @Test
    void testGetAllUsers() {
        User u1 = User.builder().id("u1").email("a@x.com").name("A").role(User.UserRole.admin).build();
        User u2 = User.builder().id("u2").email("b@x.com").name("B").role(User.UserRole.student).build();

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<AuthResponse.UserDTO> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("a@x.com", result.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById_found() {
        User u = User.builder().id("id1").email("me@x.com").name("Me").role(User.UserRole.instructor).build();
        when(userRepository.findById("id1")).thenReturn(Optional.of(u));

        AuthResponse.UserDTO dto = userService.getUserById("id1");
        assertEquals("me@x.com", dto.getEmail());
        verify(userRepository).findById("id1");
    }

    @Test
    void testGetUserById_notFound() {
        when(userRepository.findById("nope")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById("nope"));
    }

    @Test
    void testDeleteUser_success() {
        User u = User.builder().id("toDelete").email("x@x.com").name("X").role(User.UserRole.student).build();
        when(userRepository.findById("toDelete")).thenReturn(Optional.of(u));
        doNothing().when(userRepository).delete(u);

        userService.deleteUser("toDelete");

        verify(userRepository, times(1)).delete(u);
    }

    @Test
    void testDeleteUser_notFound() {
        when(userRepository.findById("bad")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser("bad"));
    }
}
