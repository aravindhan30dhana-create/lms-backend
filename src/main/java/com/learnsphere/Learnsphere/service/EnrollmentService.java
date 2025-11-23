package com.learnsphere.Learnsphere.service;

import com.learnsphere.Learnsphere.dto.EnrollmentDTO;
import com.learnsphere.Learnsphere.entity.*;
import com.learnsphere.Learnsphere.exception.ResourceAlreadyExistsException;
import com.learnsphere.Learnsphere.exception.ResourceNotFoundException;
import com.learnsphere.Learnsphere.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final CourseService courseService;
    
    @Transactional
    public EnrollmentDTO enrollInCourse(String courseId, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        if (!student.getRole().equals(User.UserRole.student)) {
            throw new IllegalArgumentException("Only students can enroll in courses");
        }
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        if (!course.getIsApproved()) {
            throw new IllegalArgumentException("Cannot enroll in unapproved course");
        }
        
        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new ResourceAlreadyExistsException("Already enrolled in this course");
        }
        
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .progress(0)
                .build();
        
        enrollment = enrollmentRepository.save(enrollment);
        
        // Update course enrollment count
        course.setEnrolledStudents(course.getEnrolledStudents() + 1);
        courseRepository.save(course);
        
        return mapToEnrollmentDTO(enrollment);
    }
    
    public List<EnrollmentDTO> getStudentEnrollments(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        return enrollmentRepository.findByStudent(student).stream()
                .map(this::mapToEnrollmentDTO)
                .collect(Collectors.toList());
    }
    
    public EnrollmentDTO getEnrollment(String courseId, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        
        return mapToEnrollmentDTO(enrollment);
    }
    
    @Transactional
    public EnrollmentDTO updateProgress(String enrollmentId, String lessonId, String studentEmail) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        if (!enrollment.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("Not authorized to update this enrollment");
        }
        
        if (!enrollment.getCompletedLessons().contains(lessonId)) {
            enrollment.getCompletedLessons().add(lessonId);
            
            // Calculate progress
            List<Lesson> courseLessons = lessonRepository.findByCourseIdOrderByOrderIndexAsc(
                    enrollment.getCourse().getId());
            int totalLessons = courseLessons.size();
            int completedCount = enrollment.getCompletedLessons().size();
            int progress = totalLessons > 0 ? (completedCount * 100) / totalLessons : 0;
            
            enrollment.setProgress(progress);
            enrollment = enrollmentRepository.save(enrollment);
        }
        
        return mapToEnrollmentDTO(enrollment);
    }
    
    @Transactional
    public EnrollmentDTO removeCompletedLesson(String enrollmentId, String lessonId, String studentEmail) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        if (!enrollment.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("Not authorized to update this enrollment");
        }
        
        enrollment.getCompletedLessons().remove(lessonId);
        
        // Recalculate progress
        List<Lesson> courseLessons = lessonRepository.findByCourseIdOrderByOrderIndexAsc(
                enrollment.getCourse().getId());
        int totalLessons = courseLessons.size();
        int completedCount = enrollment.getCompletedLessons().size();
        int progress = totalLessons > 0 ? (completedCount * 100) / totalLessons : 0;
        
        enrollment.setProgress(progress);
        enrollment = enrollmentRepository.save(enrollment);
        
        return mapToEnrollmentDTO(enrollment);
    }
    
    @Transactional
    public void unenroll(String enrollmentId, String studentEmail) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        if (!enrollment.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("Not authorized to delete this enrollment");
        }
        
        Course course = enrollment.getCourse();
        course.setEnrolledStudents(Math.max(0, course.getEnrolledStudents() - 1));
        courseRepository.save(course);
        
        enrollmentRepository.delete(enrollment);
    }
    
    private EnrollmentDTO mapToEnrollmentDTO(Enrollment enrollment) {
        return EnrollmentDTO.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent().getId())
                .courseId(enrollment.getCourse().getId())
                .progress(enrollment.getProgress())
                .completedLessons(enrollment.getCompletedLessons())
                .enrolledAt(enrollment.getEnrolledAt())
                .course(courseService.getCourseById(enrollment.getCourse().getId()))
                .build();
    }
}