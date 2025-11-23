package com.learnsphere.Learnsphere.service;
import com.learnsphere.Learnsphere.dto.*;
import com.learnsphere.Learnsphere.entity.*;
import com.learnsphere.Learnsphere.exception.ResourceNotFoundException;
import com.learnsphere.Learnsphere.exception.UnauthorizedException;
import com.learnsphere.Learnsphere.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO, String instructorEmail) {
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
        
        Course course = Course.builder()
                .title(courseDTO.getTitle())
                .description(courseDTO.getDescription())
                .instructor(instructor)
                .category(courseDTO.getCategory())
                .level(courseDTO.getLevel())
                .duration(courseDTO.getDuration())
                .price(courseDTO.getPrice())
                .thumbnail(courseDTO.getThumbnail())
                .isApproved(false)
                .enrolledStudents(0)
                .rating(0.0)
                .build();
        
        course = courseRepository.save(course);
        
        // Create lessons if provided
        if (courseDTO.getLessons() != null && !courseDTO.getLessons().isEmpty()) {
            Course finalCourse = course;
            List<Lesson> lessons = courseDTO.getLessons().stream()
                    .map(lessonDTO -> Lesson.builder()
                            .course(finalCourse)
                            .title(lessonDTO.getTitle())
                            .description(lessonDTO.getDescription())
                            .type(lessonDTO.getType())
                            .url(lessonDTO.getUrl())
                            .duration(lessonDTO.getDuration())
                            .orderIndex(lessonDTO.getOrder())
                            .build())
                    .collect(Collectors.toList());
            
            course.setLessons(lessons);
courseRepository.save(course);
        }
        
        return mapToCourseDTO(course);
    }
    
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToCourseDTO)
                .collect(Collectors.toList());
    }
    
    public List<CourseDTO> getApprovedCourses() {
        return courseRepository.findByIsApproved(true).stream()
                .map(this::mapToCourseDTO)
                .collect(Collectors.toList());
    }
    
    public CourseDTO getCourseById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return mapToCourseDTO(course);
    }
    
    public List<CourseDTO> getCoursesByInstructor(String instructorEmail) {
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
        
        return courseRepository.findByInstructor(instructor).stream()
                .map(this::mapToCourseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CourseDTO updateCourse(String id, CourseDTO courseDTO, String userEmail) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!course.getInstructor().getId().equals(user.getId()) && 
            !user.getRole().equals(User.UserRole.admin)) {
            throw new UnauthorizedException("Not authorized to update this course");
        }
        
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setCategory(courseDTO.getCategory());
        course.setLevel(courseDTO.getLevel());
        course.setDuration(courseDTO.getDuration());
        course.setPrice(courseDTO.getPrice());
        course.setThumbnail(courseDTO.getThumbnail());
        
        course = courseRepository.save(course);
        return mapToCourseDTO(course);
    }
    
    @Transactional
    public void deleteCourse(String id, String userEmail) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!course.getInstructor().getId().equals(user.getId()) && 
            !user.getRole().equals(User.UserRole.admin)) {
            throw new UnauthorizedException("Not authorized to delete this course");
        }
        
        courseRepository.delete(course);
    }
    
    @Transactional
    public CourseDTO approveCourse(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        course.setIsApproved(true);
        course = courseRepository.save(course);
        return mapToCourseDTO(course);
    }
    
    @Transactional
    public CourseDTO disapproveCourse(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        course.setIsApproved(false);
        course = courseRepository.save(course);
        return mapToCourseDTO(course);
    }
    
    public List<CourseDTO> searchCourses(String query, String category, String level) {
        List<Course> courses = courseRepository.findByIsApproved(true);
        
        if (query != null && !query.isEmpty()) {
            courses = courses.stream()
                    .filter(c -> c.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                               c.getDescription().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (category != null && !category.isEmpty() && !category.equals("all")) {
            courses = courses.stream()
                    .filter(c -> c.getCategory().equals(category))
                    .collect(Collectors.toList());
        }
        
        if (level != null && !level.isEmpty() && !level.equals("all")) {
            courses = courses.stream()
                    .filter(c -> c.getLevel().name().equals(level))
                    .collect(Collectors.toList());
        }
        
        return courses.stream()
                .map(this::mapToCourseDTO)
                .collect(Collectors.toList());
    }
    
    private CourseDTO mapToCourseDTO(Course course) {
        List<LessonDTO> lessonDTOs = lessonRepository.findByCourseIdOrderByOrderIndexAsc(course.getId())
                .stream()
                .map(this::mapToLessonDTO)
                .collect(Collectors.toList());
        
        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .instructorId(course.getInstructor().getId())
                .instructorName(course.getInstructor().getName())
                .category(course.getCategory())
                .level(course.getLevel())
                .duration(course.getDuration())
                .price(course.getPrice())
                .thumbnail(course.getThumbnail())
                .isApproved(course.getIsApproved())
                .enrolledStudents(course.getEnrolledStudents())
                .rating(course.getRating())
                .totalLessons(lessonDTOs.size())
                .lessons(lessonDTOs)
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
    
    private LessonDTO mapToLessonDTO(Lesson lesson) {
        return LessonDTO.builder()
                .id(lesson.getId())
                .courseId(lesson.getCourse().getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .type(lesson.getType())
                .url(lesson.getUrl())
                .duration(lesson.getDuration())
                .order(lesson.getOrderIndex())
                .build();
    }
}