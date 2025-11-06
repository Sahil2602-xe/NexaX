package com.project.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.demo.model.TwoFactorOTP;

@Repository
public interface TwoFactorOtpRepository extends JpaRepository<TwoFactorOTP, String> {

    // ✅ Use Optional for null-safety
    Optional<TwoFactorOTP> findByUserId(Long userId);

    // ✅ (Optional) — If you ever need to delete old OTPs for a user
    void deleteByUserId(Long userId);
}
