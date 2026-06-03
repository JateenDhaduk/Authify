package com.example.Authify.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String userName;
    private String email;
    private String role;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
}
