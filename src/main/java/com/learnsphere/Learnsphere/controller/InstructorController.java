package com.learnsphere.Learnsphere.controller;

import com.learnsphere.Learnsphere.dto.CourseDTO;
import com.learnsphere.Learnsphere.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instructor")
@RequiredArgsConstructor
@Tag(name = "Instructor", description = "Instructor endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:8081"})
public class InstructorController {
    
    private final CourseService courseService;
    
    @GetMapping("/courses")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Get instructor's courses")
    public ResponseEntity<List<CourseDTO>> getInstructorCourses(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(courseService.getCoursesByInstructor(userDetails.getUsername()));
    }
    
    @PostMapping("/courses")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Create a new course")
    public ResponseEntity<CourseDTO> createCourse(
            @Valid @RequestBody CourseDTO courseDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        CourseDTO created = courseService.createCourse(courseDTO, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/courses/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Update course")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable String id,
            @Valid @RequestBody CourseDTO courseDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        CourseDTO updated = courseService.updateCourse(id, courseDTO, userDetails.getUsername());
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/courses/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Delete course")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        courseService.deleteCourse(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}