package com.example.Authify.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String message; 
    private int status;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    private Map<String, String> errors; // For validation errors, if any
}
