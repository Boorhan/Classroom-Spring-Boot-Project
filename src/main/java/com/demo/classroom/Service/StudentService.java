package com.demo.classroom.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.classroom.Entity.Student;
import com.demo.classroom.Repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public String generateRollNumber() {
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

    public void save(Student student) {
        student.setRoll(generateRollNumber());
        studentRepository.save(student);
    }
}
