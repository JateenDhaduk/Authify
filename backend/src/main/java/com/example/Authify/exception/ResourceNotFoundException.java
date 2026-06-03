package com.example.Authify.exception;

import jakarta.validation.constraints.NotBlank;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException( String message) {
        super(message);
    }
}
