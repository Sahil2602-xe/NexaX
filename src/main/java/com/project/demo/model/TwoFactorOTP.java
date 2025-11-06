package com.project.demo.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorOTP {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)  // ✅ Auto-generate unique ID
    private String id;

    private String otp;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String jwt;

    // ✅ Optional: store when the OTP was created
    private LocalDateTime createdAt = LocalDateTime.now();

    // ✅ Optional: set OTP expiry for security
    private LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

    // Helper method (optional)
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
