package com.demo.classroom.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.classroom.DTO.ApiResponse;
import com.demo.classroom.DTO.RegistrationDTO;
import com.demo.classroom.Entity.Student;
import com.demo.classroom.Entity.Teacher;
import com.demo.classroom.Entity.User;
import com.demo.classroom.Repository.TeacherRepository;
import com.demo.classroom.Repository.UserRepository;
import com.demo.classroom.Utility.Constants;
import com.demo.classroom.Utility.Constants.Role;

import jakarta.transaction.Transactional;

@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentService studentService;

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
            studentService.save(student);
            ApiResponse<Void>  apiResponse = new ApiResponse<Void>(true, Constants.STUDENT_REG_SUCCESSFULL.getMessage());
            return apiResponse;
        } else {
            ApiResponse<Void> apiResponse = new ApiResponse<Void>(false, Constants.INVALID_ROLE.getMessage());
            return apiResponse;
        }
    }
}
