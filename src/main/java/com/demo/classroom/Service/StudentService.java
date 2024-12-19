package com.demo.classroom.Service;

import com.demo.classroom.DTO.EnrollRequest;
import com.demo.classroom.Entity.Course;
import com.demo.classroom.Entity.Student;
import com.demo.classroom.Exception.ResourceNotFoundException;
import com.demo.classroom.Repository.CourseRepository;
import com.demo.classroom.Repository.StudentRepository;
import com.demo.classroom.Security.Service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final JwtService jwtService;

    public String enrollStudentInCourse(String token, EnrollRequest enrollRequest) {
        Long studentID = jwtService.extractUserId(token);

        Student student = studentRepository.findById(studentID)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentID));

        Course course = courseRepository.findById(enrollRequest.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + enrollRequest.getCourseId()));

        if (student.getCourses().contains(course)) {
            throw new IllegalStateException("Student is already enrolled in this course.");
        }

        student.getCourses().add(course);
        course.getStudents().add(student);
        studentRepository.save(student);
        courseRepository.save(course);

        return "Student: " + student.getName()+" enrolled successfully in course: " + course.getTitle();
    }
}