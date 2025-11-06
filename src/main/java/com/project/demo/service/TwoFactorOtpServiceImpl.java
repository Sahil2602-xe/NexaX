package com.project.demo.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.demo.model.TwoFactorOTP;
import com.project.demo.model.User;
import com.project.demo.repository.TwoFactorOtpRepository;

@Service
public class TwoFactorOtpServiceImpl implements TwoFactorOtpService {

    @Autowired
    private TwoFactorOtpRepository twoFactorOtpRepository;

    @Override
    @Transactional
    public TwoFactorOTP createtwoFactorOTP(User user, String otp, String jwt) {
        // ✅ Delete any old OTPs for this user before creating new one
        TwoFactorOTP existingOtp = twoFactorOtpRepository.findByUserId(user.getId()).orElse(null);
        if (existingOtp != null) {
            twoFactorOtpRepository.delete(existingOtp);
        }

        TwoFactorOTP newOtp = new TwoFactorOTP();
        newOtp.setId(UUID.randomUUID().toString());
        newOtp.setOtp(otp);
        newOtp.setJwt(jwt);
        newOtp.setUser(user);
        newOtp.setCreatedAt(LocalDateTime.now());
        newOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // expires in 5 minutes

        return twoFactorOtpRepository.save(newOtp);
    }

    @Override
    public TwoFactorOTP findByUser(Long userId) {
        return twoFactorOtpRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public TwoFactorOTP findById(String id) {
        return twoFactorOtpRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOtp, String otp) {
        if (twoFactorOtp == null) return false;

        // ✅ Check expiry first
        if (twoFactorOtp.isExpired()) {
            twoFactorOtpRepository.deleteById(twoFactorOtp.getId());
            return false;
        }

        boolean isValid = twoFactorOtp.getOtp().equals(otp);

        if (isValid) {
            // ✅ Delete after successful verification to prevent reuse
            twoFactorOtpRepository.deleteById(twoFactorOtp.getId());
        }

        return isValid;
    }

    @Override
    @Transactional
    public void deleteTwoFactorOtp(TwoFactorOTP twoFactorOtp) {
        if (twoFactorOtp != null && twoFactorOtp.getId() != null) {
            twoFactorOtpRepository.deleteById(twoFactorOtp.getId());
        }
    }
}
