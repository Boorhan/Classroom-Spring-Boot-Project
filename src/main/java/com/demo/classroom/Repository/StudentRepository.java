package com.demo.classroom.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.classroom.Entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
