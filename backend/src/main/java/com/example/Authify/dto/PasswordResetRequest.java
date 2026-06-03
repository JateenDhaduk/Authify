package com.example.Authify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordResetRequest {
    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotNull(message = "enter the Otp")
    @Size(max = 6, min = 6 , message = "OTP must be exactly 6 digits ")
    private String otp;

    @NotNull(message = "password is required")
    private String newPassword;
}
