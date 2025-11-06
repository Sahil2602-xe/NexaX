package com.project.demo.service;

import com.project.demo.model.TwoFactorOTP;
import com.project.demo.model.User;

public interface TwoFactorOtpService {

     TwoFactorOTP createtwoFactorOTP(User user, String otp, String jwt);

     TwoFactorOTP findByUser(Long userId);

     TwoFactorOTP findById(String id);

     boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOtp, String otp);

     void deleteTwoFactorOtp(TwoFactorOTP twoFactorOtp);
}
