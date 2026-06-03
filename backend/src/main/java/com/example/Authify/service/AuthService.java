package com.example.Authify.service;

import com.example.Authify.exception.InvalidOtpException;
import com.example.Authify.exception.OtpExpiredException;
import com.example.Authify.repository.UserRepository;
import com.example.Authify.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private static final int OTP_EXPIRY_MINUTES = 10;
    @Transactional
    public void sendOTP(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email:" + email));

        if(user.isVerified()){
            throw new IllegalStateException("user is already verified");
        }

        String otp = String.format("%06d",
                new SecureRandom().nextInt(900000) + 100000);

        user.setVerifyOtp(otp);
        user.setVerifyOtpExpiry(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        userRepository.save(user);
        emailService.sendOtpToEmail(email,otp);
    }
    public void verifyOtp(String email, String otp){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("account with email is not found" + email));
        if(user.isVerified()){
            throw new IllegalStateException("user is already verified");
        }
        if(user.getVerifyOtp() == null){
            throw new InvalidOtpException("otp not found request new One");
        }
        if(user.getVerifyOtpExpiry().isBefore(LocalDateTime.now())){
            throw new OtpExpiredException("otp is expired request new one");
        }
        if(!user.getVerifyOtp().equals(otp)){
            throw new InvalidOtpException("Invalid otp enter valid Otp");
        }
        user.setVerified(true);
        user.setVerifyOtp(null);
        user.setVerifyOtpExpiry(null);
        userRepository.save(user);

        log.info("User verified successfully: {}", email);
    }

    public void sentResetPasswordOtp(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Account with this email is not found" + email));

        String otp = String.format("%06d",
                new SecureRandom().nextInt(900000) + 100000);

        user.setVerifyOtp(otp);
        user.setVerifyOtpExpiry(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        userRepository.save(user);
        emailService.sendOtpToEmail(email,otp);
    }

    public void verifyResetPasswordOtp(String email,String otp,String newPassword){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Account with this emails not found" + email));

        if(user.getVerifyOtp() == null){
            throw new InvalidOtpException("Otp not found request new one");
        }
        if(user.getVerifyOtpExpiry().isBefore(LocalDateTime.now())){
            throw new OtpExpiredException("otp is expired request new one");
        }
        if(!user.getVerifyOtp().equals(otp)){
            throw new InvalidOtpException(" Invalid otp");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVerifyOtp(null);
        user.setVerifyOtpExpiry(null);
        userRepository.save(user);

        log.info("user password successfully reset ");
    }
}
