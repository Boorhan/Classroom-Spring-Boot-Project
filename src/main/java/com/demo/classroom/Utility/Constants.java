package com.demo.classroom.Utility;

public enum Constants {

    USERNAME_TAKEN("Username is already taken."),
    EMAIL_TAKEN("Email is already taken."),
    INVALID_ROLE("Invalid role selected."),
    VALIDATION_FAILED("Validation failed."),
    TEACHER_REG_SUCCESSFULL("Teacher registered successfully"),
    STUDENT_REG_SUCCESSFULL("Student registered successfully"),
    REGISTRATION_FAILED("An error occurred during registration.");

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
    }
}
