package com.example.Authify.controller;

import com.example.Authify.dto.*;
import com.example.Authify.exception.*;
import com.example.Authify.service.AuthService;
import com.example.Authify.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.lettuce.core.RedisClient;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=" +
    "org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration," +
    "org.springframework.boot.data.redis.autoconfigure.DataRedisReactiveAutoConfiguration," +
    "org.springframework.boot.data.redis.autoconfigure.DataRedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private RedisClient redisClient;

    @MockitoBean
    private ProxyManager<byte[]> proxyManager;

    @MockitoBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockitoBean
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void registerUser_EmailAlreadyExists_Returns409() throws Exception {
        Mockito.when(userService.registerUser(any(UserRequest.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already exists"));

        String jsonRequest = "{\"username\":\"testuser\",\"email\":\"test@email.com\",\"phone\":\"1234567890\",\"password\":\"password\"}";

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    public void registerUser_UsernameAlreadyExists_Returns409() throws Exception {
        Mockito.when(userService.registerUser(any(UserRequest.class)))
                .thenThrow(new UserNameAlreadyExistsException("Username already exists"));

        String jsonRequest = "{\"username\":\"testuser\",\"email\":\"test@email.com\",\"phone\":\"1234567890\",\"password\":\"password\"}";

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username already exists"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    public void loginUser_BadCredentials_Returns401() throws Exception {
        Mockito.when(userService.loginUser(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        String jsonRequest = "{\"username\":\"testuser\",\"password\":\"wrongpassword\"}";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    public void loginUser_AccountNotVerified_Returns401() throws Exception {
        Mockito.when(userService.loginUser(any(LoginRequest.class)))
                .thenThrow(new AccountNotVerifiedException("Please verify your email before logging in"));

        String jsonRequest = "{\"username\":\"testuser\",\"password\":\"password\"}";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Please verify your email before logging in"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    public void verifyOtp_InvalidOtp_Returns400() throws Exception {
        Mockito.doThrow(new InvalidOtpException("Invalid otp enter valid Otp"))
                .when(authService).verifyOtp(any(String.class), any(String.class));

        String jsonRequest = "{\"email\":\"test@email.com\",\"otp\":\"123456\"}";

        mockMvc.perform(post("/api/auth/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid otp enter valid Otp"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    public void verifyOtp_OtpExpired_Returns410() throws Exception {
        Mockito.doThrow(new OtpExpiredException("otp is expired request new one"))
                .when(authService).verifyOtp(any(String.class), any(String.class));

        String jsonRequest = "{\"email\":\"test@email.com\",\"otp\":\"123456\"}";

        mockMvc.perform(post("/api/auth/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.message").value("otp is expired request new one"))
                .andExpect(jsonPath("$.status").value(410));
    }
}
