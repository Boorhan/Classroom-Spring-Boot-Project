package com.demo.classroom.Controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.demo.classroom.DTO.ApiResponse;
import com.demo.classroom.DTO.LoginDTO;
import com.demo.classroom.DTO.RegistrationDTO;
import com.demo.classroom.Security.Service.JwtService;
import com.demo.classroom.Service.AuthService;
import com.demo.classroom.Utility.Constants;
import com.demo.classroom.Utility.ErrorMessages;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

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
        return apiResponse.isSuccess() ? 
            ResponseEntity.status(HttpStatus.CREATED).body(apiResponse) :
            ResponseEntity.badRequest().body(apiResponse);
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
        return apiResponse.isSuccess() ? 
            ResponseEntity.status(HttpStatus.OK).body(apiResponse) :
            ResponseEntity.badRequest().body(apiResponse);
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        String accessToken = request.getHeader(Constants.AUTH_HEADER).substring(7);
        String refreshToken = request.getHeader(Constants.REFRESH_TOKEN_HEADER);
        jwtService.invalidateToken(accessToken);
        jwtService.invalidateToken(refreshToken);
        
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new ApiResponse<>(true, Constants.LOG_OUT_SUCCESSFUL.getMessage(), null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        String newAccessToken = authService.refreshAccessToken(refreshToken);

        if (newAccessToken == null && !jwtService.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Failed to generate token", null));
        }
        Map<String, String> jwtToken = new HashMap<>();
        jwtToken.put("accessToken", newAccessToken);
        jwtToken.put("refreshToken", refreshToken);
        return ResponseEntity.ok(new ApiResponse<>(true, "Token generated successfully", jwtToken));
    }
}
