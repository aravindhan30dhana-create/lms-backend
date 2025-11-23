package com.learnsphere.Learnsphere.controller;

import com.learnsphere.Learnsphere.dto.AuthResponse;
import com.learnsphere.Learnsphere.dto.CourseDTO;
import com.learnsphere.Learnsphere.service.CourseService;
import com.learnsphere.Learnsphere.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AdminController {
    
    private final UserService userService;
    private final CourseService courseService;
    
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users")
    public ResponseEntity<List<AuthResponse.UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/courses")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all courses (including unapproved)")
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }
    
    @PutMapping("/courses/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve course")
    public ResponseEntity<CourseDTO> approveCourse(@PathVariable String id) {
        return ResponseEntity.ok(courseService.approveCourse(id));
    }
    
    @PutMapping("/courses/{id}/disapprove")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Disapprove course")
    public ResponseEntity<CourseDTO> disapproveCourse(@PathVariable String id) {
        return ResponseEntity.ok(courseService.disapproveCourse(id));
    }
    
    @DeleteMapping("/courses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete any course")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id, null);
        return ResponseEntity.noContent().build();
    }
}