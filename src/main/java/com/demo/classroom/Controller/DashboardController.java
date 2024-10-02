package com.demo.classroom.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.demo.classroom.DTO.ApiResponse;
import com.demo.classroom.DTO.CourseDTO;
import com.demo.classroom.Service.CourseService;

import jakarta.validation.Valid;

import java.util.HashMap; // Make sure to import this
import java.util.List;
import java.util.ArrayList;
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

    @PostMapping(value = "/create_course", consumes = "application/json", produces = "application/json")
@PreAuthorize("hasRole('TEACHER')") 
public ResponseEntity<ApiResponse<?>> createCourse(
    @Valid @RequestBody CourseDTO courseDTO, 
    BindingResult result,
    @RequestHeader("Authorization") String token 
) {
    System.out.println("Received CourseDTO: " + courseDTO);

    if (result.hasErrors()) {
        System.out.println("Validation errors detected");

        Map<String, List<String>> errors = new HashMap<>();

        result.getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        });

        ApiResponse<Map<String, List<String>>> errorResponse = new ApiResponse<>(
            false, 
            "Validation failed", 
            errors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    // Extract the JWT token and proceed with course creation
    String jwtToken = token.substring(7);
    ApiResponse<Void> apiResponse = courseService.createCourse(courseDTO, jwtToken);
    
    return apiResponse.isSuccess() ? 
        ResponseEntity.status(HttpStatus.CREATED).body(apiResponse) :
        ResponseEntity.badRequest().body(apiResponse);
}

}
