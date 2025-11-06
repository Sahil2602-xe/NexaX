package com.project.demo.service;

import com.project.demo.domain.VerificationType;
import com.project.demo.model.ForgotPasswordToken;
import com.project.demo.model.User;

public interface ForgotPasswordService {

    ForgotPasswordToken createToken(User user,
                                    String id, String otp,
                                    VerificationType verificationType,
                                    String sendTo);

    ForgotPasswordToken findById(String id);

    ForgotPasswordToken findByUser(Long userId);

    void deleteToken(ForgotPasswordToken token);
}
