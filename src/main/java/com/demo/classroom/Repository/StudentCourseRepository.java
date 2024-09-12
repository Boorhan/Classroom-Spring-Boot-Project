package com.demo.classroom.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.classroom.Entity.StudentCourse;

public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long>{
    
}
