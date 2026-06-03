package com.example.Authify.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserApiResponse<T> {

    private String message;
    private boolean success;
    private int statusCode;
    private T data;
    private Map<String,String> errors ;

    @Builder.Default
    private LocalDateTime timeStamp = LocalDateTime.now();

    public static <T> UserApiResponse<T> success(String message){
        return UserApiResponse.<T>builder()
                .success(true)
                .statusCode(200)
                .message(message)
                .build();
    }
    public static <T> UserApiResponse<T> success(String message , T data){
        return UserApiResponse.<T>builder()
                .success(true)
                .statusCode(200)
                .data(data)
                .message(message)
                .build();
    }
    public static <T> UserApiResponse<T> success(int statusCode, String message, T data) {
        return UserApiResponse.<T>builder()
                .success(true)
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .build();
    }

}
