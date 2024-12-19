package com.demo.classroom.Controller;

import com.demo.classroom.DTO.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.demo.classroom.Service.CourseService;
import com.demo.classroom.Utility.Constants;
import com.demo.classroom.Utility.ErrorMessages;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final CourseService courseService;

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

    @PostMapping("course/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse> enrollStudentInCourse(
            @RequestBody EnrollRequest enrollRequest,
            @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String response = courseService.enrollStudentInCourse(jwtToken, enrollRequest);
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, response, null));
    }

    @GetMapping("/course/all")
    @PreAuthorize("hasRole('STUDENT')") 
    public ResponseEntity<ApiResponse<?>> getAllCourses(@RequestHeader("Authorization") String token) {
        //List<GetCourseDTO> courses = courseService.getAllCourses();

        String jwtToken = token.substring(7);
        List<EnhancedCourseResponse> courses = courseService.getAllCourses(jwtToken);
        if (courses == null || courses.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, Constants.COURSE_NOT_AVAILABLE.getMessage(), null));
        }
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, Constants.COURSES_FETCHED_SUCCESSFULLY.getMessage(), courses));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<ApiResponse> getDashboard(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        return ResponseEntity.ok().body(courseService.goToDashboard(jwtToken));
    }

    @GetMapping("/course/details")
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<ApiResponse> getCourseDetails(
            @RequestBody Map<String, Long> request,
            @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        return ResponseEntity.ok().body(courseService.getCourseDetails(jwtToken, request.get("courseId")));
    }
}
