package com.demo.classroom.Service;
import java.util.Optional;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.classroom.DTO.ApiResponse;
import com.demo.classroom.DTO.LoginDTO;
import com.demo.classroom.DTO.RegistrationDTO;
import com.demo.classroom.Entity.Student;
import com.demo.classroom.Entity.Teacher;
import com.demo.classroom.Entity.User;
import com.demo.classroom.Repository.StudentRepository;
import com.demo.classroom.Repository.TeacherRepository;
import com.demo.classroom.Repository.UserRepository;
import com.demo.classroom.Security.Service.JwtService;
import com.demo.classroom.Utility.Constants;
import com.demo.classroom.Utility.Constants.Role;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private UserRepository userRepository;

    private TeacherRepository teacherRepository;

    private StudentRepository studentRepository;

    private PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    @Autowired
    public AuthService(UserRepository userRepository, TeacherRepository teacherRepository, 
        StudentRepository studentRepository, PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager
    ){
        this.userRepository=userRepository;
        this.teacherRepository=teacherRepository;
        this.studentRepository=studentRepository;
        this.passwordEncoder=passwordEncoder;
        this.authenticationManager=authenticationManager;
    } 

    @Transactional
    public ApiResponse<Void> registerUser(RegistrationDTO request) {
        
        if (userRepository.existsByUsername(request.getUsername())) {
            return createApiResponse(false, Constants.USERNAME_TAKEN.getMessage());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return createApiResponse(false, Constants.EMAIL_TAKEN.getMessage());
        }

        try{
            if(!isValidRole(request.getRole())){
                return createApiResponse(false, Constants.INVALID_ROLE.getMessage());
            }

            User user = createUser(request);
            Role role=Role.valueOf(request.getRole().toUpperCase());
            String name = request.getName();

            if (Role.TEACHER.equals(role)) {
                registerTeacher(user, name);
                return createApiResponse(true, Constants.TEACHER_REG_SUCCESSFULL.getMessage());
            } else if (Role.STUDENT.equals(role)) {
                registerStudent(user, name);
                return createApiResponse(true, Constants.STUDENT_REG_SUCCESSFULL.getMessage());
            }
        }catch(IllegalArgumentException e){
            return createApiResponse(false, Constants.INVALID_ROLE.getMessage());
        }
        return createApiResponse(false, Constants.REGISTRATION_FAILED.getMessage());
    }
    
    @Transactional
    public ApiResponse<?> loginUser(LoginDTO request) {
        
        boolean validUsername = userRepository.existsByUsername(request.getUsername());

        if (!validUsername) {
            
            return createApiResponse(false, Constants.INVALID_USERNAME.getMessage());
        }

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        var authUser = userService.loadUserByUsername(request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username"));

        var userId = user.getId();
        var teacher = teacherRepository.findByUserId(userId);
        var student = studentRepository.findByUserId(userId);

        

        boolean isTeacher = authentication.getAuthorities().stream()
            .anyMatch(role -> role.getAuthority().equals("ROLE_TEACHER"));
        boolean isStudent = authentication.getAuthorities().stream()
            .anyMatch(role -> role.getAuthority().equals("ROLE_STUDENT"));

        if (isTeacher) {
            Map<String, String> jwtToken = Collections.singletonMap("accessToken", jwtService.generateToken(authUser, teacher.get().getId()));
            return new ApiResponse<>(true, Constants.TEACHER_LOGIN_SUCCESSFULL.getMessage(), jwtToken);
        } else if (isStudent) {
            Map<String, String> jwtToken = Collections.singletonMap("accessToken", jwtService.generateToken(authUser, student.get().getId()));
            return new ApiResponse<>(true, Constants.STUDENT_LOGIN_SUCCESSFULL.getMessage(), jwtToken);
        
        } 
        
        return createApiResponse(false, Constants.LOGIN_FAILED.getMessage());
    }

    private User createUser(RegistrationDTO request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);
    
        return user;
    }

    private void registerTeacher(User user, String name) {
        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setName(name);
        userRepository.save(user);
        teacherRepository.save(teacher);
    }

    private void registerStudent(User user, String name) {
        Student student = new Student();
        student.setUser(user);
        student.setName(name);
        student.setRoll(generateRollNumber());
        userRepository.save(user);
        studentRepository.save(student);
    }

    private boolean isValidRole(String role) {
        return Arrays.stream(Role.values()).anyMatch(r -> r.name().equalsIgnoreCase(role));
    }

    private ApiResponse<Void> createApiResponse(boolean success, String message){
        return new ApiResponse<Void>(success, message);
    }

    private String generateRollNumber() {
        String prefix = "Sc10A";
        
        Optional<Student> lastStudent = studentRepository.findFirstByOrderByRollDesc();
        
        int nextRollNumber = lastStudent.map(student -> {
            String lastRollNumber = student.getRoll();
            if (lastRollNumber == null || lastRollNumber.length() <= prefix.length()){
                return 1;
            }
            String sequentialPart = lastRollNumber.substring(prefix.length());
            return Integer.parseInt(sequentialPart) + 1; 
        }).orElse(1); 
        
        String formattedRollNumber = prefix + String.format("%03d", nextRollNumber);

        return formattedRollNumber;
    }
}
