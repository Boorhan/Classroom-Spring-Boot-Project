package com.demo.classroom.Utility;
import java.util.Arrays;

public enum Constants {

    USERNAME_TAKEN("Username is already taken."),
    EMAIL_TAKEN("Email is already taken."),
    
    TEACHER_REG_SUCCESSFULL("Teacher registered successfully"),
    TEACHER_LOGIN_SUCCESSFULL("Teacher login successful"),
    STUDENT_REG_SUCCESSFULL("Student registered successfully"),
    STUDENT_LOGIN_SUCCESSFULL("Student login successful"),

    INVALID_ROLE("Invalid role selected."),
    INVALID_USERNAME("Invalid username selected"),
    VALIDATION_FAILED("Validation failed."),
    REGISTRATION_FAILED("An error occurred during registration."),
    LOGIN_FAILED("An error occurred during login."),
    
    EMPTY_PASSWORD("Password cannot be empty"),
    UPPERCASE_REQUIRED("Password must contain at least one uppercase letter"),
    LOWERCASE_REQUIRED("Password must contain at least one lowercase letter"),
    DIGIT_REQUIRED("Password must contain at least one digit"),
    SPECIAL_CHAR_REQUIRED("Password must contain at least one special character"),
    LENGTH_REQUIRED("Password must be at least 8 characters long");

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
    
    public enum PublicEndpoints {

        SIGNUP("/signup"),
        LOGIN("/login");;
        
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
