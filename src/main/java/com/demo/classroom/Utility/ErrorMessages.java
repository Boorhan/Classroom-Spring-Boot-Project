package com.demo.classroom.Utility;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


public class ErrorMessages {

    public static Map<String, List<String>> constructErrorMessage(BindingResult result) {
        return result.getFieldErrors().stream()
            .collect(Collectors.groupingBy(
                FieldError::getField,
                Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
            ));
    }
}
