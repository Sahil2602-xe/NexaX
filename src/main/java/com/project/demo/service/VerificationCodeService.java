package com.project.demo.service;

import com.project.demo.domain.VerificationType;
import com.project.demo.model.User;
import com.project.demo.model.VerificationCode;

public interface VerificationCodeService {
    VerificationCode sendVerificationCode(User user, VerificationType verificationType);

    VerificationCode getVerificationCodeById(Long id) throws Exception;

    VerificationCode getVerificationCodeByUser(Long userId);

    void deleteVerificationCodeById(VerificationCode  verificationCode);
}
