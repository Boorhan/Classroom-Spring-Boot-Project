package com.demo.classroom.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.classroom.Entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByUserId(Long id);
    Optional<Teacher> findByUserId(Long userId);
}
