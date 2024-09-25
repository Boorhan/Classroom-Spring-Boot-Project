package com.demo.classroom.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.demo.classroom.DTO.ApiResponse;
import com.demo.classroom.DTO.LoginDTO;
import com.demo.classroom.DTO.RegistrationDTO;
import com.demo.classroom.Repository.UserRepository;
import com.demo.classroom.Service.AuthService;
import com.demo.classroom.Utility.Constants;
import com.demo.classroom.Utility.ErrorMessages;

import jakarta.validation.Valid;


@RestController
public class AuthController {
    private final AuthService authService;
    
    private final UserRepository userRepository;
    
    private AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.userRepository=userRepository;
        this.authenticationManager=authenticationManager;
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
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO loginDTO, BindingResult result) {

        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            result.getFieldErrors().forEach(error -> {
                errorMessage.append("Field '").append(error.getField()).append("' - ")
                            .append(error.getDefaultMessage()).append(". ");
            });
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }

        boolean validUsername = userRepository.existsByUsername(loginDTO.getUsername());

        if (!validUsername) {
            return ResponseEntity.badRequest().body("Invalid Username.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            boolean isTeacher = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_TEACHER"));
            boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_STUDENT"));

            if (isTeacher) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("Login as teacher is successful");
            } else if (isStudent) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("Login as student is successful");
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("User role not recognized");
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password incorrect");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }
}
