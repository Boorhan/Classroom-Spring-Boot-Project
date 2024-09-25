package com.demo.classroom.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.classroom.Entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    // Method to find the student with the highest roll number
    Optional<Student> findFirstByOrderByRollDesc();
}
