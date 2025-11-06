package com.project.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.project.demo.model.ForgotPasswordToken;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordToken, String> {

    ForgotPasswordToken findByUserId(Long userId);

    // ✅ NEW: Delete existing tokens directly by userId
    @Transactional
    void deleteByUserId(Long userId);
}
