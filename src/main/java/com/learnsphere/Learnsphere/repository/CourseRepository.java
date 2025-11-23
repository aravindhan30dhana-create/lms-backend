package com.learnsphere.Learnsphere.repository;

import com.learnsphere.Learnsphere.entity.Course;
import com.learnsphere.Learnsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findByInstructor(User instructor);
    List<Course> findByIsApproved(Boolean isApproved);
    List<Course> findByCategory(String category);
    List<Course> findByLevel(Course.CourseLevel level);
    List<Course> findByIsApprovedAndTitleContainingIgnoreCase(Boolean isApproved, String title);
}