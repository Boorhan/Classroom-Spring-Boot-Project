package com.demo.classroom.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.classroom.Entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByUserId(Long id);
}
