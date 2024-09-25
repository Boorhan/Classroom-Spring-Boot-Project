package com.demo.classroom.Utility;
import java.util.Arrays;

public enum Constants {

    USERNAME_TAKEN("Username is already taken."),
    EMAIL_TAKEN("Email is already taken."),
    
    TEACHER_REG_SUCCESSFULL("Teacher registered successfully"),
    STUDENT_REG_SUCCESSFULL("Student registered successfully"),

    INVALID_ROLE("Invalid role selected."),
    VALIDATION_FAILED("Validation failed."),
    REGISTRATION_FAILED("An error occurred during registration."),
    
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

        SIGNUP("/signup");
        
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
