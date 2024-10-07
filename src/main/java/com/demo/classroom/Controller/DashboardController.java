package com.demo.classroom.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.demo.classroom.DTO.ApiResponse;
import com.demo.classroom.DTO.CourseDTO;
import com.demo.classroom.DTO.GetCourseDTO;
import com.demo.classroom.Service.CourseService;
import com.demo.classroom.Utility.Constants;
import com.demo.classroom.Utility.ErrorMessages;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;

@RestController
public class DashboardController {

    @Autowired
    private CourseService courseService;

    @PostMapping(value = "/course/create", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('TEACHER')") 
    public ResponseEntity<ApiResponse<?>> createCourse(
        @Valid @RequestBody CourseDTO courseDTO, 
        BindingResult result,
        @RequestHeader("Authorization") String token 
    ) {
        if (result.hasErrors()) {
            Map<String, List<String>> errors = ErrorMessages.constructErrorMessages(result);
                ApiResponse<Map<String, List<String>>> errorResponse = new ApiResponse<>(
                    false, 
                    Constants.VALIDATION_FAILED.getMessage(), 
                    errors
                );
            return ResponseEntity.badRequest().body(errorResponse);
        }

        String jwtToken = token.substring(7);
        ApiResponse<Void> apiResponse = courseService.createCourse(courseDTO, jwtToken);

        return apiResponse.isSuccess() ? 
            ResponseEntity.status(HttpStatus.CREATED).body(apiResponse) :
            ResponseEntity.badRequest().body(apiResponse);
    }

    @GetMapping("/course/all")
    @PreAuthorize("hasRole('STUDENT')") 
    public ResponseEntity<ApiResponse<?>> getAllCourses() {
        List<GetCourseDTO> courses = courseService.getAllCourses();
        if (courses == null || courses.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, Constants.COURSE_NOT_AVAILABLE.getMessage(), null));
        }
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, Constants.COURSES_FETCHED_SUCCESSFULLY.getMessage(), courses));
    }
}
