package com.example.Authify.controller;

import com.example.Authify.dto.*;
import com.example.Authify.service.AuthService;
import com.example.Authify.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
        @Autowired
        private final UserService userService;
        private final AuthService authService;

        @PostMapping("/register")
        @Operation(summary = "Register a new user", description = "Endpoint to register a new user with username, email, phone, and password.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "User registered successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "409", description = "Email or username already exists")

        })
        public ResponseEntity<UserApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRequest userRequest) {
                // Placeholder for user registration logic
                UserResponse userResponse = userService.registerUser(userRequest);
                return ResponseEntity.ok(UserApiResponse.success(201, "Account created successfully", userResponse));
        }

        @PostMapping("/login")
        @Operation(summary = "Login a user", description = "Endpoint to login a user with email and password.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User logged in successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
        })
        public ResponseEntity<UserApiResponse<AuthResponse>> loginUser(@Valid @RequestBody LoginRequest request) {
                // Placeholder for user login logic
                AuthResponse authResponse = userService.loginUser(request);
                return ResponseEntity.ok(UserApiResponse.success("Login successful", authResponse));
        }

        @GetMapping("/profile")
        @Operation(summary = "user profile ", description = "Endpoint to user profile with jwt authentication")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "user found"),
                        @ApiResponse(responseCode = "400", description = "User not fount"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
        })
        public ResponseEntity<UserApiResponse<UserResponse>> profile(@AuthenticationPrincipal UserDetails userDetails) {
                UserResponse userResponse = userService.userProfile(userDetails);
                return ResponseEntity.ok(UserApiResponse.success("WellCome back", userResponse));
        }

        @PostMapping("/generate-otp")
        @Operation(summary = "send-otp", description = "EndPoint to send otp to user email address")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
                        @ApiResponse(responseCode = "404", description = "User not found"),
                        @ApiResponse(responseCode = "400", description = "Validation error")
        })
        public ResponseEntity<UserApiResponse<Void>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
                authService.sendOTP(request.getEmail());
                return ResponseEntity.ok(UserApiResponse.success("OTP sent successfully"));
        }

        @PostMapping("/verify-otp")
        @Operation(summary = "Verify OTP", description = "Verifies the OTP and activates the account")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Account verified successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid OTP"),
                        @ApiResponse(responseCode = "410", description = "OTP expired")
        })
        public ResponseEntity<UserApiResponse<Void>> verifyOtp(
                        @Valid @RequestBody VerifyOtpRequest request) {
                authService.verifyOtp(request.getEmail(), request.getOtp());
                return ResponseEntity.ok(UserApiResponse.success("Account verified successfully"));
        }

        @PostMapping("/forgot-password")
        @Operation(summary = "reset Password", description = "Endpoint to sent reset password request")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "OTP successfully sent to Email "),
                        @ApiResponse(responseCode = "400", description = "Invalid OTP"),
                        @ApiResponse(responseCode = "410", description = "OTP expired")
        })
        public ResponseEntity<UserApiResponse<Void>> forgetPassword(@Valid @RequestBody SendOtpRequest request) {
                authService.sentResetPasswordOtp(request.getEmail());
                return ResponseEntity.ok(UserApiResponse.success("OTP sent successfully"));
        }

        @PostMapping("/reset-password")
        @Operation(summary = "Verify OTP", description = "Verifies the OTP and activates the account")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Account verified successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid OTP"),
                        @ApiResponse(responseCode = "410", description = "OTP expired")
        })
        public ResponseEntity<UserApiResponse<Void>> resetPassword(
                        @Valid @RequestBody PasswordResetRequest request) {
                authService.verifyResetPasswordOtp(request.getEmail(), request.getOtp(), request.getNewPassword());
                return ResponseEntity.ok(UserApiResponse.success("successfully reset password"));
        }

}
