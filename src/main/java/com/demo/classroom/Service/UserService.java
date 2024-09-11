package com.demo.classroom.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.classroom.Entity.User;
import com.demo.classroom.Repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository; // You will need to create this repository

    public void registerUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }
}
