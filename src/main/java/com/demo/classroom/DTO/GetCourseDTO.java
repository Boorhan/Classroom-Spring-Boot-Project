package com.demo.classroom.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCourseDTO  {
    private Long id;
    private String title;
    private List<String> teacherNames; 
    private List<BookDTO> books; 
}
