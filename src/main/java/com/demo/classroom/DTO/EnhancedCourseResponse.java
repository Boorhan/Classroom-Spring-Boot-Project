package com.demo.classroom.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedCourseResponse {
    private String courseTitle;
    private Long courseId;
    private List<BookDTO> books;
    private String teacherName;
    private int numberOfStudentsEnrolled;
    private boolean isStudentEnrolled;
}
