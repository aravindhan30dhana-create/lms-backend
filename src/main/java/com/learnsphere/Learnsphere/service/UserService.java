package com.learnsphere.Learnsphere.service;

import com.learnsphere.Learnsphere.dto.AuthResponse;
import com.learnsphere.Learnsphere.entity.User;
import com.learnsphere.Learnsphere.exception.ResourceNotFoundException;
import com.learnsphere.Learnsphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public List<AuthResponse.UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserDTO)
                .collect(Collectors.toList());
    }
    
    public AuthResponse.UserDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserDTO(user);
    }
    
    public AuthResponse.UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserDTO(user);
    }
    
    public List<AuthResponse.UserDTO> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(this::mapToUserDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }
    
    private AuthResponse.UserDTO mapToUserDTO(User user) {
        return AuthResponse.UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .avatar(user.getAvatar())
                .build();
    }
}