package com.demo.classroom.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
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
import com.demo.classroom.Service.StudentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/req")
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationRequest request, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation failed: " + result.getAllErrors());
        }


        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        String role = request.getRole();
        String name = request.getName();

        userRepository.save(user);

        if ("teacher".equalsIgnoreCase(role)) {
            Teacher teacher = new Teacher();
            teacher.setUser(user);
            teacher.setName(name);
            teacherRepository.save(teacher);
            return ResponseEntity.ok("Teacher registered successfully");
        } else if ("student".equalsIgnoreCase(role)) {
            Student student = new Student();
            student.setUser(user); 
            student.setName(name);
            studentService.save(student);
            return ResponseEntity.ok("Student registered successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid role selected");
        }
    }
}
