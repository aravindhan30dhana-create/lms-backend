package com.learnsphere.Learnsphere.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDTO {
    private String id;
    private String studentId;
    private String courseId;
    private Integer progress;
    private List<String> completedLessons;
    private LocalDateTime enrolledAt;
    private CourseDTO course;
}