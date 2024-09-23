package com.demo.classroom.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.classroom.DTO.RegistrationDTO;
import com.demo.classroom.Service.RegistrationService;
import com.demo.classroom.Utility.ErrorMessages;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/req")
public class AuthController {
    @Autowired
    private RegistrationService registrationService;

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationDTO request, BindingResult result) {

        if (result.hasErrors()) {
            String errorMessage = ErrorMessages.constructErrorMessage(result);
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }

        String response = registrationService.registerUser(request);
        if (response.contains("successfully")) {
             return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
