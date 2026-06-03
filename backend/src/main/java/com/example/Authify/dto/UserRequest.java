package com.example.Authify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
public class UserRequest {
  @NotBlank(message = "Username is mandatory")
  private String username;
  
  @Email
  @NotBlank(message = "Email is mandatory")
  private String email;
  
  @NotBlank(message = "Phone is mandatory")
  private String phone;
  
  @NotBlank(message = "Password is mandatory")
  private String password;
}
