package com.demo.classroom.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import java.util.List;
import java.util.ArrayList;
import com.demo.classroom.Utility.Constants;

@Constraint(validatedBy = com.demo.classroom.Validation.ValidPassword.PasswordValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message() default "Invalid password format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    public static class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

        private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
        private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
        private static final Pattern DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
        private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[@#$%^&+=!].*");
        private static final Pattern LENGTH_PATTERN = Pattern.compile(".{8,}");
    
        @Override
        public void initialize(ValidPassword constraintAnnotation) {
        }
    
        @Override
        public boolean isValid(String password, ConstraintValidatorContext context) {
            List<String> messages = new ArrayList<>(); 
    
            if (password == null || password.trim().isEmpty()) {
                messages.add(Constants.EMPTY_PASSWORD.getMessage());
            } else {
                ValidationRule[] rules = {
                    new ValidationRule(LENGTH_PATTERN, Constants.LENGTH_REQUIRED.getMessage()),
                    new ValidationRule(UPPERCASE_PATTERN, Constants.UPPERCASE_REQUIRED.getMessage()),
                    new ValidationRule(LOWERCASE_PATTERN, Constants.LOWERCASE_REQUIRED.getMessage()),
                    new ValidationRule(DIGIT_PATTERN, Constants.DIGIT_REQUIRED.getMessage()),
                    new ValidationRule(SPECIAL_CHAR_PATTERN, Constants.SPECIAL_CHAR_REQUIRED.getMessage())
                };
                for (ValidationRule rule : rules) {
                    if (!rule.pattern.matcher(password).matches()) {
                        messages.add(rule.message);
                    }
                }
            }
    
            if (!messages.isEmpty()) {
                context.disableDefaultConstraintViolation();
                for (String message : messages) {
                    context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                }
                return false; 
            }
    
            return true; 
        }
        private static class ValidationRule {
            private final Pattern pattern;
            private final String message;
    
            public ValidationRule(Pattern pattern, String message) {
                this.pattern = pattern;
                this.message = message;
            }
        }
    }
}
