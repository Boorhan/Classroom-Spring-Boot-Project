package com.demo.classroom.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.classroom.Entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long>{
    
}
