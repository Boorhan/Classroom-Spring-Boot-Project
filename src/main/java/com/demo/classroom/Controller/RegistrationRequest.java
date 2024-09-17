package com.demo.classroom.Controller;
import lombok.Data;

@Data
public class RegistrationRequest {
    private String username;
    private String email;
    private String password;
    private String role; 
    private String name;  
}
