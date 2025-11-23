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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management endpoints")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:8081"})
public class CourseController {
    
    private final CourseService courseService;
    
    @GetMapping
    @Operation(summary = "Get all approved courses")
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getApprovedCourses());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable String id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search courses")
    public ResponseEntity<List<CourseDTO>> searchCourses(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String level) {
        return ResponseEntity.ok(courseService.searchCourses(query, category, level));
    }
}