package com.demo.classroom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class DBTestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db-check")
    public String checkDatabaseConnection() {
        try {
            // Execute a simple query to test the connection
            jdbcTemplate.execute("SELECT 1");
            return "Database is connected!";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}