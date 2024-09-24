package com.demo.classroom.DTO;
import com.demo.classroom.Utility.Constants.Role;
import com.demo.classroom.Validation.ValidPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationDTO {
    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @ValidPassword(message = "Password must be at least 8 characters long, contain an uppercase letter, a digit, and a special character")
    private String password;

    @NotBlank(message = "Role is mandatory")
    private String role; 

    @NotBlank(message = "Name is mandatory")
    private String name;  
}
