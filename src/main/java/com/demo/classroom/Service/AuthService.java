package com.demo.classroom.Service;
import java.util.Optional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final TeacherRepository teacherRepository;

    private final StudentRepository studentRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserService userService;

    
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
                return createApiResponse(true, Constants.TEACHER_REG_SUCCESSFUL.getMessage());
            } else if (Role.STUDENT.equals(role)) {
                registerStudent(user, name);
                return createApiResponse(true, Constants.STUDENT_REG_SUCCESSFUL.getMessage());
            }
        }catch(IllegalArgumentException e){
            return createApiResponse(false, Constants.INVALID_ROLE.getMessage());
        }
        return createApiResponse(false, Constants.REGISTRATION_FAILED.getMessage());
    }
    
    @Transactional
    public ApiResponse<?> loginUser(LoginDTO request) {

        String userName = request.getUsername();

        if (!isUsernameValid(userName)) {
            return createApiResponse(false, Constants.INVALID_USERNAME.getMessage());
        }

        Authentication authentication = authenticateUser(request);

        UserDetails authUser = userService.loadUserByUsername(userName);

        User user = getUserByUsername(userName);

        Optional<Teacher> teacher = getTeacherByUserId(user.getId());
        Optional<Student> student = getStudentByUserId(user.getId());

        if (isTeacher(authentication)) {
            return createTeacherLoginResponse(authUser, teacher.orElseThrow());
        } else if (isStudent(authentication)) {
            return createStudentLoginResponse(authUser, student.orElseThrow());
        }

        return createApiResponse(false, Constants.LOGIN_FAILED.getMessage());
    }

    private boolean isUsernameValid(String username) {
        return userRepository.existsByUsername(username);
    }

    private Authentication authenticateUser(LoginDTO request) {
        return authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(Constants.INVALID_USERNAME.getMessage()));
    }

    private Optional<Teacher> getTeacherByUserId(Long userId) {
        return teacherRepository.findByUserId(userId);
    }

    private Optional<Student> getStudentByUserId(Long userId) {
        return studentRepository.findByUserId(userId);
    }

    private boolean isTeacher(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .anyMatch(role -> role.getAuthority().equals("ROLE_TEACHER"));
    }

    private boolean isStudent(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .anyMatch(role -> role.getAuthority().equals("ROLE_STUDENT"));
    }

    private ApiResponse<Map<String, String>> createTeacherLoginResponse(UserDetails authUser, Teacher teacher) {
        Map<String, String> jwtToken = new HashMap<>();
        jwtToken.put("accessToken", jwtService.generateToken(authUser, teacher.getId()));
        jwtToken.put("refreshToken", jwtService.generateRefreshToken(authUser));
        return new ApiResponse<>(true, Constants.TEACHER_LOGIN_SUCCESSFUL.getMessage(), jwtToken);
    }

    private ApiResponse<Map<String, String>> createStudentLoginResponse(UserDetails authUser, Student student) {
        Map<String, String> jwtToken = new HashMap<>();
        jwtToken.put("accessToken", jwtService.generateToken(authUser, student.getId()));
        jwtToken.put("refreshToken", jwtService.generateRefreshToken(authUser));
        return new ApiResponse<>(true, Constants.STUDENT_LOGIN_SUCCESSFUL.getMessage(), jwtToken);
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

    public String refreshAccessToken(String refreshToken) {
    
        if (refreshToken == null){
            return null;
        }

        String username = jwtService.extractUsername(refreshToken); 
        UserDetails userDetails = userService.loadUserByUsername(username);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            return null; 
        }

        return jwtService.generateToken(userDetails);
    }  
}
