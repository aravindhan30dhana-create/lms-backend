package com.learnsphere.Learnsphere.dto;

import com.learnsphere.Learnsphere.entity.Course;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {
    private String id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    private String instructorId;
    private String instructorName;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    @NotNull(message = "Level is required")
    private Course.CourseLevel level;
    
    private String duration;
    
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private BigDecimal price;
    
    private String thumbnail;
    private Boolean isApproved;
    private Integer enrolledStudents;
    private Double rating;
    private Integer totalLessons;
    
    private List<LessonDTO> lessons;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}