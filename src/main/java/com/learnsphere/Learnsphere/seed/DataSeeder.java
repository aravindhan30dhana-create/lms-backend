package com.learnsphere.Learnsphere.seed;

import com.learnsphere.Learnsphere.entity.*;
import com.learnsphere.Learnsphere.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    //@Transactional  // Add this annotation
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping seed data.");
            return;
        }
        
        log.info("Starting database seeding...");
        seedUsers();
        seedCourses();
        seedEnrollments();
        log.info("Database seeding completed successfully!");
    }
    
   private void seedUsers() {
    List<User> users = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();
    
    // Admin users
    users.add(User.builder()
            .id("admin-1")
            .email("admin@edulearn.com")
            .password(passwordEncoder.encode("admin123"))
            .name("Sarah Johnson")
            .role(User.UserRole.admin)
            .avatar("https://api.dicebear.com/7.x/avataaars/svg?seed=admin1")
            .createdAt(now)
            .updatedAt(now)
            .build());
    
    users.add(User.builder()
            .id("admin-2")
            .email("admin2@edulearn.com")
            .password(passwordEncoder.encode("admin123"))
            .name("Michael Chen")
            .role(User.UserRole.admin)
            .avatar("https://api.dicebear.com/7.x/avataaars/svg?seed=admin2")
            .createdAt(now)
            .updatedAt(now)
            .build());
    
    users.add(User.builder()
            .id("admin-3")
            .email("admin3@edulearn.com")
            .password(passwordEncoder.encode("admin123"))
            .name("Emily Rodriguez")
            .role(User.UserRole.admin)
            .avatar("https://api.dicebear.com/7.x/avataaars/svg?seed=admin3")
            .createdAt(now)
            .updatedAt(now)
            .build());
    
    // Instructor users
    users.add(User.builder()
            .id("instructor-1")
            .email("instructor@edulearn.com")
            .password(passwordEncoder.encode("instructor123"))
            .name("Dr. James Wilson")
            .role(User.UserRole.instructor)
            .avatar("https://api.dicebear.com/7.x/avataaars/svg?seed=instructor1")
            .createdAt(now)
            .updatedAt(now)
            .build());
    
    users.add(User.builder()
            .id("instructor-2")
            .email("instructor2@edulearn.com")
            .password(passwordEncoder.encode("instructor123"))
            .name("Prof. Maria Garcia")
            .role(User.UserRole.instructor)
            .avatar("https://api.dicebear.com/7.x/avataaars/svg?seed=instructor2")
            .createdAt(now)
            .updatedAt(now)
            .build());
    
    users.add(User.builder()
            .id("instructor-3")
            .email("instructor3@edulearn.com")
            .password(passwordEncoder.encode("instructor123"))
            .name("Dr. David Kim")
            .role(User.UserRole.instructor)
            .avatar("https://api.dicebear.com/7.x/avataaars/svg?seed=instructor3")
            .createdAt(now)
            .updatedAt(now)
            .build());
    
    // Student users
    users.add(User.builder()
            .id("student-1")
            .email("student@edulearn.com")
            .password(passwordEncoder.encode("student123"))
            .name("Alex Thompson")
            .role(User.UserRole.student)
            .avatar("https://api.dicebear.com/7.x/avataaars/svg?seed=student1")
            .createdAt(now)
            .updatedAt(now)
            .build());
    
    users.add(User.builder()
            .id("student-2")
            .email("student2@edulearn.com")
            .password(passwordEncoder.encode("student123"))
            .name("Sophie Martinez")
            .role(User.UserRole.student)
            .avatar("https://api.dicebear.com/7.x/avataaars/svg?seed=student2")
            .createdAt(now)
            .updatedAt(now)
            .build());
    
    users.add(User.builder()
            .id("student-3")
            .email("student3@edulearn.com")
            .password(passwordEncoder.encode("student123"))
            .name("Ryan Lee")
            .role(User.UserRole.student)
            .avatar("https://api.dicebear.com/7.x/avataaars/svg?seed=student3")
            .createdAt(now)
            .updatedAt(now)
            .build());
    
    users.add(User.builder()
            .id("student-4")
            .email("student4@edulearn.com")
            .password(passwordEncoder.encode("student123"))
            .name("Jessica Brown")
            .role(User.UserRole.student)
            .avatar("https://api.dicebear.com/7.x/avataaars/svg?seed=student4")
            .createdAt(now)
            .updatedAt(now)
            .build());
    
    users.add(User.builder()
            .id("student-5")
            .email("student5@edulearn.com")
            .password(passwordEncoder.encode("student123"))
            .name("Daniel White")
            .role(User.UserRole.student)
            .avatar("https://api.dicebear.com/7.x/avataaars/svg?seed=student5")
            .createdAt(now)
            .updatedAt(now)
            .build());
    
    userRepository.saveAll(users);
    userRepository.flush();
    
    // CRITICAL DEBUGGING: Verify users were actually saved
    long count = userRepository.count();
    log.info("Seeded {} users, database now contains {} users", users.size(), count);
    
    if (count == 0) {
        log.error("NO USERS WERE SAVED! Check entity constraints.");
        throw new RuntimeException("User seeding failed - no users in database");
    }
}

private void seedCourses() {
    User instructor1 = userRepository.findById("instructor-1")
            .orElseThrow(() -> new RuntimeException("Instructor 1 not found in database"));
    User instructor2 = userRepository.findById("instructor-2")
            .orElseThrow(() -> new RuntimeException("Instructor 2 not found in database"));
    User instructor3 = userRepository.findById("instructor-3")
            .orElseThrow(() -> new RuntimeException("Instructor 3 not found in database"));
    
    log.info("Found all instructors, proceeding with course seeding");
    
    // Course 1: Web Development
    Course course1 = Course.builder()
            .id("course-1")
            .title("Complete Web Development Bootcamp")
            .description("Master web development from scratch with HTML, CSS, JavaScript, React, and Node.js")
            .instructor(instructor1)
            .category("Web Development")
            .level(Course.CourseLevel.Beginner)
            .duration("40 hours")
            .price(new BigDecimal("99.99"))
            .thumbnail("https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=800&h=450&fit=crop")
            .isApproved(true)
            .enrolledStudents(245)
            .rating(4.8)
            .build();
    courseRepository.saveAndFlush(course1);  // Changed from save() to saveAndFlush()
    
    List<Lesson> lessons1 = Arrays.asList(
        Lesson.builder()
            .id("lesson-1")
            .course(course1)
            .title("Introduction to Web Development")
            .description("Overview of web technologies and development roadmap")
            .type(Lesson.ContentType.video)
            .url("https://www.youtube.com/embed/UB1O30fR-EE")
            .duration("15:30")
            .orderIndex(1)
            .build(),
        Lesson.builder()
            .id("lesson-2")
            .course(course1)
            .title("HTML Basics")
            .description("Learn HTML tags, elements, and document structure")
            .type(Lesson.ContentType.video)
            .url("https://www.youtube.com/embed/qz0aGYrrlhU")
            .duration("22:45")
            .orderIndex(2)
            .build(),
        Lesson.builder()
            .id("lesson-3")
            .course(course1)
            .title("CSS Fundamentals")
            .description("Styling web pages with CSS properties and selectors")
            .type(Lesson.ContentType.video)
            .url("https://www.youtube.com/embed/1PnVor36_40")
            .duration("28:15")
            .orderIndex(3)
            .build()
    );
    lessonRepository.saveAll(lessons1);
    lessonRepository.flush();  // Add flush for lessons too
    
    // Course 2: Data Science
    Course course2 = Course.builder()
            .id("course-2")
            .title("Data Science with Python")
            .description("Learn data analysis, visualization, and machine learning with Python libraries")
            .instructor(instructor2)
            .category("Data Science")
            .level(Course.CourseLevel.Intermediate)
            .duration("35 hours")
            .price(new BigDecimal("129.99"))
            .thumbnail("https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&h=450&fit=crop")
            .isApproved(true)
            .enrolledStudents(189)
            .rating(4.9)
            .build();
    courseRepository.saveAndFlush(course2);  // Changed
    
    List<Lesson> lessons2 = Arrays.asList(
        Lesson.builder()
            .id("lesson-4")
            .course(course2)
            .title("Python Programming Basics")
            .description("Variables, data types, and control structures in Python")
            .type(Lesson.ContentType.video)
            .url("https://www.youtube.com/embed/rfscVS0vtbw")
            .duration("35:20")
            .orderIndex(1)
            .build(),
        Lesson.builder()
            .id("lesson-5")
            .course(course2)
            .title("NumPy for Data Analysis")
            .description("Working with arrays and numerical operations")
            .type(Lesson.ContentType.video)
            .url("https://www.youtube.com/embed/QUT1VHiLmmI")
            .duration("42:10")
            .orderIndex(2)
            .build(),
        Lesson.builder()
            .id("lesson-6")
            .course(course2)
            .title("Data Visualization with Matplotlib")
            .description("Creating charts and graphs for data insights")
            .type(Lesson.ContentType.video)
            .url("https://www.youtube.com/embed/3Xc3CA655Y4")
            .duration("38:55")
            .orderIndex(3)
            .build()
    );
    lessonRepository.saveAll(lessons2);
    lessonRepository.flush();  // Add flush
    
    // Course 3: UI/UX Design
    Course course3 = Course.builder()
            .id("course-3")
            .title("UI/UX Design Fundamentals")
            .description("Create stunning user interfaces and experiences using modern design principles")
            .instructor(instructor3)
            .category("Design")
            .level(Course.CourseLevel.Beginner)
            .duration("25 hours")
            .price(new BigDecimal("79.99"))
            .thumbnail("https://images.unsplash.com/photo-1561070791-2526d30994b5?w=800&h=450&fit=crop")
            .isApproved(true)
            .enrolledStudents(312)
            .rating(4.7)
            .build();
    courseRepository.saveAndFlush(course3);  // Changed
    
    List<Lesson> lessons3 = Arrays.asList(
        Lesson.builder()
            .id("lesson-7")
            .course(course3)
            .title("Design Thinking Process")
            .description("Understanding user-centered design methodology")
            .type(Lesson.ContentType.video)
            .url("https://www.youtube.com/embed/_r0VX-aU_T8")
            .duration("25:40")
            .orderIndex(1)
            .build(),
        Lesson.builder()
            .id("lesson-8")
            .course(course3)
            .title("Wireframing and Prototyping")
            .description("Creating low and high-fidelity prototypes")
            .type(Lesson.ContentType.video)
            .url("https://www.youtube.com/embed/qpH7-KFWZRI")
            .duration("32:25")
            .orderIndex(2)
            .build(),
        Lesson.builder()
            .id("lesson-9")
            .course(course3)
            .title("Color Theory and Typography")
            .description("Mastering visual design principles")
            .type(Lesson.ContentType.video)
            .url("https://www.youtube.com/embed/AvgCkHrcj90")
            .duration("28:50")
            .orderIndex(3)
            .build()
    );
    lessonRepository.saveAll(lessons3);
    lessonRepository.flush();  // Add flush
    
    // Course 4: Mobile Development
    Course course4 = Course.builder()
            .id("course-4")
            .title("Mobile App Development with React Native")
            .description("Build cross-platform mobile applications using React Native and Expo")
            .instructor(instructor1)
            .category("Mobile Development")
            .level(Course.CourseLevel.Intermediate)
            .duration("45 hours")
            .price(new BigDecimal("149.99"))
            .thumbnail("https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=800&h=450&fit=crop")
            .isApproved(true)
            .enrolledStudents(156)
            .rating(4.6)
            .build();
    courseRepository.saveAndFlush(course4);  // Changed
    
    List<Lesson> lessons4 = Arrays.asList(
        Lesson.builder()
            .id("lesson-10")
            .course(course4)
            .title("React Native Setup")
            .description("Environment setup and project initialization")
            .type(Lesson.ContentType.video)
            .url("https://www.youtube.com/embed/0-S5a0eXPoc")
            .duration("18:30")
            .orderIndex(1)
            .build(),
        Lesson.builder()
            .id("lesson-11")
            .course(course4)
            .title("Components and Props")
            .description("Building reusable React Native components")
            .type(Lesson.ContentType.video)
            .url("https://www.youtube.com/embed/qSRrxpdMpVc")
            .duration("45:15")
            .orderIndex(2)
            .build(),
        Lesson.builder()
            .id("lesson-12")
            .course(course4)
            .title("Navigation in React Native")
            .description("Implementing navigation with React Navigation")
            .type(Lesson.ContentType.video)
            .url("https://www.youtube.com/embed/nQVCkqvU1uE")
            .duration("40:20")
            .orderIndex(3)
            .build()
    );
    lessonRepository.saveAll(lessons4);
    lessonRepository.flush();  // Add flush
    
    // Course 5: Digital Marketing
    Course course5 = Course.builder()
            .id("course-5")
            .title("Digital Marketing Masterclass")
            .description("Master SEO, social media marketing, content marketing, and analytics")
            .instructor(instructor2)
            .category("Marketing")
            .level(Course.CourseLevel.Beginner)
            .duration("30 hours")
            .price(new BigDecimal("89.99"))
            .thumbnail("https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&h=450&fit=crop")
            .isApproved(true)
            .enrolledStudents(278)
            .rating(4.5)
            .build();
    courseRepository.saveAndFlush(course5);  // Changed
    
    List<Lesson> lessons5 = Arrays.asList(
        Lesson.builder()
            .id("lesson-13")
            .course(course5)
            .title("SEO Fundamentals")
            .description("Search engine optimization best practices")
            .type(Lesson.ContentType.video)
            .url("https://www.youtube.com/embed/DvwS7cV9GmQ")
            .duration("30:45")
            .orderIndex(1)
            .build(),
        Lesson.builder()
            .id("lesson-14")
            .course(course5)
            .title("Social Media Marketing")
            .description("Creating effective social media campaigns")
            .type(Lesson.ContentType.video)
            .url("https://www.youtube.com/embed/WKLkxT8WIzg")
            .duration("35:10")
            .orderIndex(2)
            .build(),
        Lesson.builder()
            .id("lesson-15")
            .course(course5)
            .title("Google Analytics Mastery")
            .description("Track and analyze website performance")
            .type(Lesson.ContentType.video)
            .url("https://www.youtube.com/embed/gBeMELnxdIg")
            .duration("42:30")
            .orderIndex(3)
            .build()
    );
    lessonRepository.saveAll(lessons5);
    lessonRepository.flush();  // Add flush
    
    log.info("Seeded 5 courses with their lessons");
}

    
    private void seedEnrollments() {
        User student1 = userRepository.findById("student-1")
                .orElseThrow(() -> new RuntimeException("Student 1 not found"));
        User student2 = userRepository.findById("student-2")
                .orElseThrow(() -> new RuntimeException("Student 2 not found"));
        User student3 = userRepository.findById("student-3")
                .orElseThrow(() -> new RuntimeException("Student 3 not found"));
        User student4 = userRepository.findById("student-4")
                .orElseThrow(() -> new RuntimeException("Student 4 not found"));
        User student5 = userRepository.findById("student-5")
                .orElseThrow(() -> new RuntimeException("Student 5 not found"));
        
        Course course1 = courseRepository.findById("course-1")
                .orElseThrow(() -> new RuntimeException("Course 1 not found"));
        Course course2 = courseRepository.findById("course-2")
                .orElseThrow(() -> new RuntimeException("Course 2 not found"));
        Course course3 = courseRepository.findById("course-3")
                .orElseThrow(() -> new RuntimeException("Course 3 not found"));
        Course course4 = courseRepository.findById("course-4")
                .orElseThrow(() -> new RuntimeException("Course 4 not found"));
        Course course5 = courseRepository.findById("course-5")
                .orElseThrow(() -> new RuntimeException("Course 5 not found"));
        
        List<Enrollment> enrollments = new ArrayList<>();
        
        enrollments.add(Enrollment.builder()
                .id("enroll-1")
                .student(student1)
                .course(course1)
                .progress(45)
                .completedLessons(new ArrayList<>(Arrays.asList("lesson-1", "lesson-2")))
                .build());
        
        enrollments.add(Enrollment.builder()
                .id("enroll-2")
                .student(student1)
                .course(course3)
                .progress(75)
                .completedLessons(new ArrayList<>(Arrays.asList("lesson-7", "lesson-8", "lesson-9")))
                .build());
        
        enrollments.add(Enrollment.builder()
                .id("enroll-3")
                .student(student2)
                .course(course2)
                .progress(33)
                .completedLessons(new ArrayList<>(Arrays.asList("lesson-4")))
                .build());
        
        enrollments.add(Enrollment.builder()
                .id("enroll-4")
                .student(student2)
                .course(course5)
                .progress(66)
                .completedLessons(new ArrayList<>(Arrays.asList("lesson-13", "lesson-14")))
                .build());
        
        enrollments.add(Enrollment.builder()
                .id("enroll-5")
                .student(student3)
                .course(course1)
                .progress(100)
                .completedLessons(new ArrayList<>(Arrays.asList("lesson-1", "lesson-2", "lesson-3")))
                .build());
        
        enrollments.add(Enrollment.builder()
                .id("enroll-6")
                .student(student4)
                .course(course4)
                .progress(20)
                .completedLessons(new ArrayList<>(Arrays.asList("lesson-10")))
                .build());
        
        enrollments.add(Enrollment.builder()
                .id("enroll-7")
                .student(student5)
                .course(course3)
                .progress(50)
                .completedLessons(new ArrayList<>(Arrays.asList("lesson-7", "lesson-8")))
                .build());
        
        enrollmentRepository.saveAll(enrollments);
        log.info("Seeded {} enrollments", enrollments.size());
    }
}
