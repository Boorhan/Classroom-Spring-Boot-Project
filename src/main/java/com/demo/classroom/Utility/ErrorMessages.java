package com.demo.classroom.Utility;

import org.springframework.validation.BindingResult;

public class ErrorMessages {

    public static String constructErrorMessage(BindingResult result) {
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        result.getFieldErrors().forEach(error -> {
            errorMessage.append("Field '").append(error.getField()).append("' - ")
                        .append(error.getDefaultMessage()).append(". ");
        });
        return errorMessage.toString();
    }
}
