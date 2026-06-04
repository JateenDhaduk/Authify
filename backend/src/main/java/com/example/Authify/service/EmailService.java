package com.example.Authify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendOtpToEmail(String email, String otp){
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("your Authentication code");
            message.setText("Your OTP is: " + otp + "\n\n" +
                    "This code expires in 10 minutes.\n" +
                    "If you did not request this, ignore this email."
            );
            mailSender.send(message);
            log.info("OTP sent to Email successfully.");
        }
        catch (Exception e){
            log.error("Failed to send OTP email to {}: {}", email, e.getMessage());
            log.warn("\n========================================================\n" +
                    "[PORTFOLIO DEMO MODE] SMTP port is blocked (common on Render Free Tier).\n" +
                    "To complete your registration, retrieve the OTP directly here:\n" +
                    ">>> EMAIL: {}\n" +
                    ">>> YOUR OTP IS: {}\n" +
                    "========================================================\n", email, otp);
            // We do not throw an exception here so that the user registration 
            // can still proceed to the OTP verification screen in the demo!
        }
    }
}
