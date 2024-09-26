package com.demo.classroom.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.demo.classroom.DTO.ApiResponse;
import com.demo.classroom.DTO.LoginDTO;
import com.demo.classroom.DTO.RegistrationDTO;
import com.demo.classroom.Service.AuthService;
import com.demo.classroom.Utility.Constants;
import com.demo.classroom.Utility.ErrorMessages;

import jakarta.validation.Valid;



@RestController
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }   

    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse<?>> register(
        @Valid @RequestBody RegistrationDTO request, 
        BindingResult result
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

        ApiResponse<Void> apiResponse = authService.registerUser(request);
        if (apiResponse.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } else {
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }


    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginDTO request, BindingResult result) {

        if (result.hasErrors()) {
            Map<String, List<String>> errors = ErrorMessages.constructErrorMessages(result);
            ApiResponse<Map<String, List<String>>> errorResponse = new ApiResponse<>(
                false, 
                Constants.VALIDATION_FAILED.getMessage(), 
                errors
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }

        ApiResponse<?> apiResponse = authService.loginUser(request);

        if (apiResponse.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } else {
            return ResponseEntity.badRequest().body(apiResponse);
        }
    
    }
}
