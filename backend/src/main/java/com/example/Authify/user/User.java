package com.example.Authify.user;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "password", "verifyOtp", "resetOtp" })
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @NotBlank(message = "Username is mandatory")
  @Column(unique = true, nullable = false)
  private String username;

  @Email
  @NotBlank(message = "Email is mandatory")
  @Column(unique = true, nullable = false)
  private String email;
  @NotBlank(message = "Phone is mandatory")
  private String phone;
  @NotBlank(message = "Password is mandatory")
  private String password;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  private UserRole role = UserRole.USER;

  @Builder.Default
  private boolean isVerified = false;


  private String verifyOtp;
  private LocalDateTime verifyOtpExpiry;
  private String resetOtp;
  private Long resetOtpExpiry;

  @CreationTimestamp
  @Column(updatable = false)
  private Timestamp createdAt;

  @UpdateTimestamp
  private Timestamp updatedAt;

}
