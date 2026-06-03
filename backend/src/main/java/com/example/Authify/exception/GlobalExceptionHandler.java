package com.example.Authify.exception;
import java.util.LinkedHashMap;
import java.util.Map;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.Authify.dto.ErrorResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ErrorResponse.builder()
                                                .message(ex.getMessage())
                                                .status(HttpStatus.CONFLICT.value())
                                                .build());
        }

        @ExceptionHandler(UserNameAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleUserNameAlreadyExistsException(UserNameAlreadyExistsException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ErrorResponse.builder()
                                                .message(ex.getMessage())
                                                .status(HttpStatus.CONFLICT.value())
                                                .build());
        }

        @ExceptionHandler(UsernameNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUserNameNotFoundException(UsernameNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ErrorResponse.builder()
                                                .message(ex.getMessage())
                                                .status(HttpStatus.NOT_FOUND.value())
                                                .build());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
                Map<String, String> fieldErrors = new LinkedHashMap<>();
                ex.getBindingResult().getFieldErrors().forEach(error -> {
                        fieldErrors.put(error.getField(), error.getDefaultMessage());
                });
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ErrorResponse.builder()
                                                .message("Validation failed")
                                                .status(HttpStatus.BAD_REQUEST.value())
                                                .errors(fieldErrors)
                                                .build());
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(ErrorResponse.builder()
                                                .message("Invalid email or password")
                                                .status(HttpStatus.UNAUTHORIZED.value())
                                                .build());
        }
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .message("Invalid JWT signature") // Masked exact error
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build()
        );
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtToken(ExpiredJwtException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .message("JWT token has expired. Please log in again.") // Custom clean message
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build()
        );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .message("Invalid JWT token") // Masked exact error
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build()
        );
    }
    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOtpException(InvalidOtpException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ErrorResponse.builder()
                            .message(ex.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ErrorResponse> handleOtpExpiredException(OtpExpiredException ex){
            return ResponseEntity.status(HttpStatus.GONE).body(
                    ErrorResponse.builder()
                            .message(ex.getMessage())
                            .status(HttpStatus.GONE.value())
                            .build()
            );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ErrorResponse.builder()
                            .message(ex.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
    }
    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotVerifiedException(AccountNotVerifiedException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ErrorResponse.builder()
                            .message("Please verify your email before logging in")
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .build()
            );
    }

}
