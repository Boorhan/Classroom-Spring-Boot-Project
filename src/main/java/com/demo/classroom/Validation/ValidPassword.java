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

@Constraint(validatedBy = com.demo.classroom.Validation.ValidPassword.PasswordValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message() default "Invalid password format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    public static class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
        private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        );

        @Override
        public void initialize(ValidPassword constraintAnnotation) {
        }

        @Override
        public boolean isValid(String password, ConstraintValidatorContext context) {
            if (password == null || password.trim().isEmpty())  {
                return false;
            }
            return password.matches(PASSWORD_PATTERN.toString());
        }
    }
}
