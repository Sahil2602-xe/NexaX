package com.project.demo.controller;

import com.project.demo.domain.VerificationType;
import com.project.demo.model.TwoFactorOTP;
import com.project.demo.model.User;
import com.project.demo.service.EmailService;
import com.project.demo.service.TwoFactorOtpService;
import com.project.demo.service.UserService;
import com.project.demo.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        String otp = OtpUtils.generateOTP();

        // Create and save OTP record
        twoFactorOtpService.createtwoFactorOTP(user, otp, jwt);

        // Send email
        emailService.sendVerificationOtpEmail(user.getEmail(), otp);

        return new ResponseEntity<>("2FA OTP sent successfully to your email.", HttpStatus.OK);
    }

    // ✅ Step 2: Verify OTP and enable 2FA
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verify2FAOtp(
            @RequestHeader("Authorization") String jwt,
            @RequestParam String otp) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findByUser(user.getId());

        if (twoFactorOTP == null)
            throw new Exception("No OTP found for user.");

        boolean isVerified = twoFactorOtpService.verifyTwoFactorOtp(twoFactorOTP, otp);

        if (isVerified) {
            user.setTwoFactorEnabled(true);
            user.setSendTo(VerificationType.EMAIL); // ✅ fixed enum assignment
            userService.save(user); // ✅ fixed method name
            return new ResponseEntity<>("Two-Factor Authentication enabled successfully!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid or expired OTP.", HttpStatus.BAD_REQUEST);
        }
    }
}
