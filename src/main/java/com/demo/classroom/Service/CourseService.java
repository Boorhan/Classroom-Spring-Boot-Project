package com.demo.classroom.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.demo.classroom.DTO.*;
import com.demo.classroom.Entity.Student;
import com.demo.classroom.Exception.ResourceNotFoundException;
import com.demo.classroom.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import com.demo.classroom.Entity.Book;
import com.demo.classroom.Entity.Course;
import com.demo.classroom.Entity.Teacher;
import com.demo.classroom.Repository.CourseRepository;
import com.demo.classroom.Repository.TeacherRepository;
import com.demo.classroom.Security.Service.JwtService;
import com.demo.classroom.Utility.Constants;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    private final TeacherRepository teacherRepository;

    private final JwtService jwtService;

    private final StudentRepository studentRepository;

    @Transactional
    public ApiResponse<Void> createCourse(CourseDTO courseDTO, String token) {
        Long teacherId = jwtService.extractUserId(token);  
        Optional<Teacher> teacher = teacherRepository.findByUserId(teacherId);
       
        if (teacher.isEmpty()) {
           return createApiResponse(false, Constants.USER_NOT_FOUND.getMessage());
        }

        Course course = new Course();
        course.setTitle(courseDTO.getTitle());
        
        List<Book> books = new ArrayList<>();
        if (courseDTO.getBooks() != null) {
            for (BookDTO bookDTO : courseDTO.getBooks()) {
                Book book = new Book();
                book.setName(bookDTO.getName());
                book.setAuthor(bookDTO.getAuthor());
                book.setCourse(course); 
                books.add(book);
            }
        }
        course.setBooks(books);
        
        course.getTeachers().add(teacher.get());
        teacher.get().getCourses().add(course);
        courseRepository.save(course);
        return createApiResponse(true, Constants.COURSE_CREATED_SUCCESSFULLY.getMessage());
    }

    public List<GetCourseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll(); 
        List<GetCourseDTO> courseDTOs = new ArrayList<>();
    
        for (Course course : courses) {
            List<String> teacherNames = course.getTeachers().stream()
                .map(teacher -> teacher.getName()) 
                .collect(Collectors.toList());

            
            List<BookDTO> bookDTOs = course.getBooks().stream()
            .map(book -> new BookDTO(book.getName(), book.getAuthor()))
            .collect(Collectors.toList());
        
    
            GetCourseDTO getCourseDTO = new GetCourseDTO(course.getId(), course.getTitle(), teacherNames, bookDTOs);
            courseDTOs.add(getCourseDTO);
        }
    
        return courseDTOs;
    }

    public List<EnhancedCourseResponse> getAllCourses(String token) {
        Long studentId = jwtService.extractUserId(token);
        Student currentStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        List<Course> courses = courseRepository.findAll();

        return courses.stream()
                .map(course -> {
                    List<BookDTO> books = course.getBooks().stream()
                            .map(book -> new BookDTO(book.getName(), book.getAuthor()))
                            .collect(Collectors.toList());

                    String teacherName = course.getTeachers().isEmpty()
                            ? "No Teacher Assigned"
                            : course.getTeachers().get(0).getName();

                    int numberOfStudentsEnrolled = course.getStudents().size();

                    boolean isStudentEnrolled = course.getStudents().contains(currentStudent);

                    return new EnhancedCourseResponse(
                            course.getTitle(),
                            course.getId(),
                            books,
                            teacherName,
                            numberOfStudentsEnrolled,
                            isStudentEnrolled
                    );
                })
                .collect(Collectors.toList());
    }

    private ApiResponse<Void> createApiResponse(boolean success, String message){
        return new ApiResponse<Void>(success, message);
    }
}
