package com.example.Authify.service;

import com.example.Authify.dto.AuthResponse;
import com.example.Authify.dto.LoginRequest;
import com.example.Authify.dto.UserRequest;
import com.example.Authify.dto.UserResponse;
import com.example.Authify.exception.AccountNotVerifiedException;
import com.example.Authify.exception.ResourceNotFoundException;
import com.example.Authify.exception.EmailAlreadyExistsException;
import com.example.Authify.exception.UserNameAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import com.example.Authify.repository.UserRepository;
import com.example.Authify.security.JwtUtils;
import com.example.Authify.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    @Lazy
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    @Transactional
    public UserResponse registerUser(UserRequest userRequest) {
        if(userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if(userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new UserNameAlreadyExistsException("Username already exists");
        }
        User tempUser = User.builder()
                .phone(userRequest.getPhone())
                .email(userRequest.getEmail())
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .build();
        userRepository.save(tempUser);
        return mapToUserResponse(tempUser);
    }
    public UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isVerified(user.isVerified())
                .build();
    }

    public AuthResponse loginUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword())
        );
        UserDetails userDetails =(UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow( () -> new ResourceNotFoundException("User not found"));

        if(!user.isVerified()){
            throw new AccountNotVerifiedException(
                    "Please verify your email before logging in");
        }

        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = refreshTokenService.generateRefreshToken(user.getEmail());
        return AuthResponse.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getExpirationMs() / 1000)
                .build();
    }

    public UserResponse userProfile(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow( () -> new UsernameNotFoundException("user not found"));
        return mapToUserResponse(user);
    }


}