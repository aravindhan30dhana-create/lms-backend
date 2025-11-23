package com.learnsphere.Learnsphere.service;

import com.learnsphere.Learnsphere.dto.AuthRequest;
import com.learnsphere.Learnsphere.dto.AuthResponse;
import com.learnsphere.Learnsphere.dto.SignupRequest;
import com.learnsphere.Learnsphere.entity.User;
import com.learnsphere.Learnsphere.exception.ResourceAlreadyExistsException;
import com.learnsphere.Learnsphere.repository.UserRepository;
import com.learnsphere.Learnsphere.security.JwtTokenProvider;
import java.util.Collections;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    
@Transactional
public AuthResponse signup(SignupRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new ResourceAlreadyExistsException("Email already registered");
    }

    String avatar = "https://api.dicebear.com/7.x/avataaars/svg?seed=" + request.getEmail();

    User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .avatar(avatar)
            .build();

    user = userRepository.save(user);

    // ✅ Build a fake authentication token manually
    org.springframework.security.core.userdetails.User springUser =
            new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase())
                    )
            );

    Authentication authentication = new UsernamePasswordAuthenticationToken(
            springUser, null, springUser.getAuthorities()
    );

    // ✅ Generate JWT directly
    String token = tokenProvider.generateToken(authentication);

    return AuthResponse.builder()
            .token(token)
            .user(mapToUserDTO(user))
            .build();
}

    
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        String token = tokenProvider.generateToken(authentication);
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return AuthResponse.builder()
                .token(token)
                .user(mapToUserDTO(user))
                .build();
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