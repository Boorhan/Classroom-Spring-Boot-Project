package com.demo.classroom.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    @NotBlank(message = "Course must have a title.")
    private String title;

    private List<BookDTO> books;

}

