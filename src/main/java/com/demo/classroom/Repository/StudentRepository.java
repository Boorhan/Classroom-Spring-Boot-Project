package com.demo.classroom.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.classroom.Entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findFirstByOrderByRollDesc();
    boolean existsByUserId(Long id);
    Optional<Student> findByUserId(Long userId);
}
