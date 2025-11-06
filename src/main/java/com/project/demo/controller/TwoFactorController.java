package com.project.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.demo.domain.VerificationType;
import com.project.demo.model.TwoFactorOTP;
import com.project.demo.model.User;
import com.project.demo.service.EmailService;
import com.project.demo.service.TwoFactorOtpService;
import com.project.demo.service.UserService;
import com.project.demo.utils.OtpUtils;

@RestController
@RequestMapping("/api/users/2fa")
public class TwoFactorController {

    @Autowired
    private UserService userService;

    @Autowired
    private TwoFactorOtpService twoFactorOtpService;

    @Autowired
    private EmailService emailService;

    // ✅ Step 1: Send OTP to user email
    @PostMapping("/send-otp")
    public ResponseEntity<String> send2FAOtp(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);

        // Delete any old OTP to avoid stale object issues
        TwoFactorOTP existingOtp = twoFactorOtpService.findByUser(user.getId());
        if (existingOtp != null) {
            twoFactorOtpService.deleteTwoFactorOtp(existingOtp);
        }

        // Generate new OTP
        String otp = OtpUtils.generateOTP();
        twoFactorOtpService.createtwoFactorOTP(user, otp, jwt);

        // Send via email
        emailService.sendVerificationOtpEmail(user.getEmail(), otp);

        return new ResponseEntity<>("✅ 2FA OTP sent successfully to your email.", HttpStatus.OK);
    }

    // ✅ Step 2: Verify OTP and enable 2FA (wrapped in a transaction)
    @PostMapping("/verify-otp")
    @Transactional
    public ResponseEntity<String> verify2FAOtp(
            @RequestHeader("Authorization") String jwt,
            @RequestParam String otp) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findByUser(user.getId());

        if (twoFactorOTP == null) {
            throw new Exception("No OTP found for this user. Please request a new one.");
        }

        boolean isVerified = twoFactorOtpService.verifyTwoFactorOtp(twoFactorOTP, otp);

        if (isVerified) {
            // ✅ Update user 2FA settings safely
            user.getTwoFactorAuth().setEnabled(true);
            user.getTwoFactorAuth().setSendTo(VerificationType.EMAIL);
            userService.save(user);

            // ✅ Delete OTP after successful verification
            twoFactorOtpService.deleteTwoFactorOtp(twoFactorOTP);

            return new ResponseEntity<>("✅ Two-Factor Authentication enabled successfully!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("❌ Invalid or expired OTP. Please try again.", HttpStatus.BAD_REQUEST);
        }
    }
}
