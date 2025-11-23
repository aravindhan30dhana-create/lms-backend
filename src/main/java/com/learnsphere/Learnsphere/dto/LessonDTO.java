package com.learnsphere.Learnsphere.dto;

import com.learnsphere.Learnsphere.entity.Lesson;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDTO {
    private String id;
    
    private String courseId;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Content type is required")
    private Lesson.ContentType type;
    
    @NotBlank(message = "Content URL is required")
    private String url;
    
    private String duration;
    
    @NotNull(message = "Order is required")
    private Integer order;
}