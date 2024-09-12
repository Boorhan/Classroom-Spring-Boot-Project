package com.demo.classroom.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.classroom.Entity.TeacherCourse;

public interface TeacherCourseRepository extends JpaRepository<TeacherCourse, Long> {
    
}
