package com.learnsphere.Learnsphere.controller;

import com.learnsphere.Learnsphere.dto.EnrollmentDTO;
import com.learnsphere.Learnsphere.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
@Tag(name = "Student", description = "Student endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:8081"})
public class StudentController {
    
    private final EnrollmentService enrollmentService;
    
    @GetMapping("/enrollments")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get student enrollments")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollments(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(enrollmentService.getStudentEnrollments(userDetails.getUsername()));
    }
    
    @PostMapping("/enroll/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Enroll in a course")
    public ResponseEntity<EnrollmentDTO> enrollInCourse(
            @PathVariable String courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        EnrollmentDTO enrollment = enrollmentService.enrollInCourse(courseId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
    }
    
    @GetMapping("/enrollments/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get enrollment for specific course")
    public ResponseEntity<EnrollmentDTO> getEnrollment(
            @PathVariable String courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(enrollmentService.getEnrollment(courseId, userDetails.getUsername()));
    }
    
    @PutMapping("/enrollments/{enrollmentId}/complete-lesson")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Mark lesson as completed")
    public ResponseEntity<EnrollmentDTO> completeLesson(
            @PathVariable String enrollmentId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        String lessonId = body.get("lessonId");
        EnrollmentDTO updated = enrollmentService.updateProgress(enrollmentId, lessonId, userDetails.getUsername());
        return ResponseEntity.ok(updated);
    }
    
    @PutMapping("/enrollments/{enrollmentId}/uncomplete-lesson")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Remove lesson from completed")
    public ResponseEntity<EnrollmentDTO> uncompleteLesson(
            @PathVariable String enrollmentId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        String lessonId = body.get("lessonId");
        EnrollmentDTO updated = enrollmentService.removeCompletedLesson(enrollmentId, lessonId, userDetails.getUsername());
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/enrollments/{enrollmentId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Unenroll from course")
    public ResponseEntity<Void> unenroll(
            @PathVariable String enrollmentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        enrollmentService.unenroll(enrollmentId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}