package com.demo.classroom.Service;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.classroom.DTO.ApiResponse;
import com.demo.classroom.DTO.RegistrationDTO;
import com.demo.classroom.Entity.Student;
import com.demo.classroom.Entity.Teacher;
import com.demo.classroom.Entity.User;
import com.demo.classroom.Repository.StudentRepository;
import com.demo.classroom.Repository.TeacherRepository;
import com.demo.classroom.Repository.UserRepository;
import com.demo.classroom.Utility.Constants;
import com.demo.classroom.Utility.Constants.Role;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public ApiResponse<Void> registerUser(RegistrationDTO request) {
        
        if (userRepository.existsByUsername(request.getUsername())) {
            ApiResponse<Void> apiResponse = new ApiResponse<Void>(false, Constants.USERNAME_TAKEN.getMessage());
            return apiResponse;
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            ApiResponse<Void> apiResponse = new ApiResponse<Void>(false, Constants.EMAIL_TAKEN.getMessage());
            return apiResponse;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        Role role = request.getRole();
        String name = request.getName();

        if (Role.TEACHER.equals(role)) {
            Teacher teacher = new Teacher();
            teacher.setUser(user);
            teacher.setName(name);
            userRepository.save(user);
            teacherRepository.save(teacher);
            ApiResponse<Void> apiResponse = new ApiResponse<Void>(true, Constants.TEACHER_REG_SUCCESSFULL.getMessage());
            return apiResponse;
        } else if (Role.STUDENT.equals(role)) {
            Student student = new Student();
            student.setUser(user);
            student.setName(name);
            userRepository.save(user);
            student.setRoll(generateRollNumber());
            studentRepository.save(student);
            ApiResponse<Void>  apiResponse = new ApiResponse<Void>(true, Constants.STUDENT_REG_SUCCESSFULL.getMessage());
            return apiResponse;
        } else {
            ApiResponse<Void> apiResponse = new ApiResponse<Void>(false, Constants.INVALID_ROLE.getMessage());
            return apiResponse;
        }

        
    }
    private String generateRollNumber() {
        String prefix = "Sc10A";
        
            Optional<Student> lastStudent = studentRepository.findFirstByOrderByRollDesc();
        
            int nextRollNumber = lastStudent.map(student -> {
                String lastRollNumber = student.getRoll();
                if (lastRollNumber == null || lastRollNumber.length() <= prefix.length()) {
                    return 1;
                }
                String sequentialPart = lastRollNumber.substring(prefix.length());
                return Integer.parseInt(sequentialPart) + 1; 
            }).orElse(1); 
        
            
            String formattedRollNumber = String.format("%03d", nextRollNumber);
        
            return prefix + formattedRollNumber;
    }
}
