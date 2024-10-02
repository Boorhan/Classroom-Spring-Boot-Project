package com.demo.classroom.Utility;
import java.util.Arrays;

public enum Constants {

    USERNAME_TAKEN("Username is already taken."),
    USER_NOT_FOUND("User not found."),
    EMAIL_TAKEN("Email is already taken."),
    ALREADY_LOGGED_IN("User is already logged in."),

    TEACHER_REG_SUCCESSFUL("Teacher registered successfully"),
    TEACHER_LOGIN_SUCCESSFUL("Teacher login successful"),
    STUDENT_REG_SUCCESSFUL("Student registered successfully"),
    STUDENT_LOGIN_SUCCESSFUL("Student login successful"),
    LOG_OUT_SUCCESSFUL("Logout successful"),
    COURSE_CREATED_SUCCESSFULLY("Course created successfully"),

    INVALID_ROLE("Invalid role selected."),
    INVALID_USERNAME("Invalid username selected"),
    INVALID_PASSWORD("Invalid password"),
    VALIDATION_FAILED("Validation failed."),
    REGISTRATION_FAILED("An error occurred during registration."),
    LOGIN_FAILED("An error occurred during login."),

    EMPTY_PASSWORD("Password cannot be empty"),
    UPPERCASE_REQUIRED("Password must contain at least one uppercase letter"),
    LOWERCASE_REQUIRED("Password must contain at least one lowercase letter"),
    DIGIT_REQUIRED("Password must contain at least one digit"),
    SPECIAL_CHAR_REQUIRED("Password must contain at least one special character"),
    LENGTH_REQUIRED("Password must be at least 8 characters long"),

    INVALID_TOKEN_FORMAT("Invalid token format."),
    TOKEN_EXPIRED("Token has expired."),
    UNSUPPORTED_TOKEN("Unsupported token."),
    AUTHENTICATION_FAILED("Authentication failed."), 
    REFRESH_TOKEN_INVALID("Refresh token is invalid or expired"), 
    REFRESH_TOKEN_REQUIRED( "Refresh token is required"),
    
    INTERNAL_SERVER_ERROR("An error occurred while processing the request");

    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
    public static final String CONTENT_TYPE = "application/json";

    private final String message;

    Constants(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public enum Role {
        TEACHER,
        STUDENT
    }

    public enum ExpirationTime {
        ACCESS_TOKEN(90000000L),
        REFRESH_TOKEN(86400000L);

        private final long value;
        ExpirationTime(long value) {
            this.value = value;
        }
        public long getValue() {
            return value;
        }
    }
    
    public enum PublicEndpoints {

        SIGNUP("/signup"),
        LOGIN("/login");
        
        private final String path;
    
        PublicEndpoints(String path) {
            this.path = path;
        }
    
        public String getPath() {
            return path;
        }

        public static String[] getAllPaths() {
            return Arrays.stream(PublicEndpoints.values())
                    .map(PublicEndpoints::getPath)
                    .toArray(String[]::new); 
        }
    }
}
