package com.demo.classroom.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.classroom.Entity.Student;
import com.demo.classroom.Entity.Teacher;
import com.demo.classroom.Entity.User;
import com.demo.classroom.Repository.StudentRepository;
import com.demo.classroom.Repository.TeacherRepository;
import com.demo.classroom.Repository.UserRepository;

@RestController
@RequestMapping("/req")
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject the password encoder

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest request) {
        // Extract user and role from the request
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // Encode the password before saving
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        String role = request.getRole();
        String name = request.getName();

        // Save the user data
        userRepository.save(user);

        // Handle role-specific registration
        if ("teacher".equalsIgnoreCase(role)) {
            Teacher teacher = new Teacher();
            teacher.setUser(user); // Associate the teacher with the user
            teacher.setName(name);
            teacherRepository.save(teacher);
            return ResponseEntity.ok("Teacher registered successfully");
        } else if ("student".equalsIgnoreCase(role)) {
            Student student = new Student();
            student.setUser(user); // Associate the student with the user
            student.setName(name);
            studentRepository.save(student);
            return ResponseEntity.ok("Student registered successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid role selected");
        }
    }
}
