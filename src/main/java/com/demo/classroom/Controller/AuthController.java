package com.demo.classroom.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.classroom.Config.JwtService;
import com.demo.classroom.DTO.LoginDTO;
import com.demo.classroom.DTO.RegistrationDTO;
import com.demo.classroom.Entity.Student;
import com.demo.classroom.Entity.Teacher;
import com.demo.classroom.Entity.User;
import com.demo.classroom.Repository.TeacherRepository;
import com.demo.classroom.Repository.UserRepository;
import com.demo.classroom.Service.StudentService;
import com.demo.classroom.Service.UserService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/req")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @GetMapping(value= "/dashboard")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("You are in the dashboard");
    }
    

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationDTO request, BindingResult result) {

        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            result.getFieldErrors().forEach(error -> {
                errorMessage.append("Field '").append(error.getField()).append("' - ")
                            .append(error.getDefaultMessage()).append(". ");
            });
            return ResponseEntity.badRequest().body(errorMessage.toString());
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

        if ("teacher".equalsIgnoreCase(role)) {
            Teacher teacher = new Teacher();
            teacher.setUser(user);
            teacher.setName(name);
            userRepository.save(user);
            teacherRepository.save(teacher);
            return ResponseEntity.ok("Teacher registered successfully");
        } else if ("student".equalsIgnoreCase(role)) {
            Student student = new Student();
            student.setUser(user); 
            student.setName(name);
            userRepository.save(user);
            studentService.save(student);
            return ResponseEntity.ok("Student registered successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid role selected");
        }
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO loginDTO, BindingResult result) {

        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            result.getFieldErrors().forEach(error -> {
                errorMessage.append("Field '").append(error.getField()).append("' - ")
                            .append(error.getDefaultMessage()).append(". ");
            });
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }

        boolean validUsername = userRepository.existsByUsername(loginDTO.getUsername());

        if (!validUsername) {
            return ResponseEntity.badRequest().body("Invalid Username.");
        }

        try {
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );

            var authUser = userService.loadUserByUsername(loginDTO.getUsername());

            var jwtToken = jwtService.generateToken(authUser);

            boolean isTeacher = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_TEACHER"));
            boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_STUDENT"));

            if (isTeacher) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("Login as teacher is successful. Token: "+jwtToken);
            } else if (isStudent) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("Login as student is successful. Token: "+jwtToken);
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("User role not recognized");
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password incorrect");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }
}
