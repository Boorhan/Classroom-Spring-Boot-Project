package com.demo.classroom.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data; 
    

    public ApiResponse(boolean success, String message) {
        this(success, message, null);
    }

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message != null ? message:"";
        this.data = data;
    }
}
