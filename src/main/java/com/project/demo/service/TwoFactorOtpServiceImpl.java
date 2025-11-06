package com.project.demo.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.demo.model.TwoFactorOTP;
import com.project.demo.model.User;
import com.project.demo.repository.TwoFactorOtpRepository;

@Service
public class TwoFactorOtpServiceImpl implements TwoFactorOtpService {

    @Autowired
    private TwoFactorOtpRepository twoFactorOtpRepository;

    @Override
    public TwoFactorOTP createtwoFactorOTP(User user, String otp, String jwt) {
        TwoFactorOTP twoFactorOtp = new TwoFactorOTP();
        twoFactorOtp.setId(UUID.randomUUID().toString());
        twoFactorOtp.setOtp(otp);
        twoFactorOtp.setJwt(jwt);
        twoFactorOtp.setUser(user);

        return twoFactorOtpRepository.save(twoFactorOtp);
    }

    @Override
    public TwoFactorOTP findByUser(Long userId) {
        // ✅ FIXED: unwrap the Optional safely
        return twoFactorOtpRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public TwoFactorOTP findById(String id) {
        // ✅ Already correct
        return twoFactorOtpRepository.findById(id).orElse(null);
    }

    @Override
public boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOtp, String otp) {
    boolean isValid = twoFactorOtp != null && twoFactorOtp.getOtp().equals(otp);

    if (isValid) {
        // ✅ Delete OTP after verification, ensuring it's fresh from DB
        TwoFactorOTP freshOtp = twoFactorOtpRepository.findById(twoFactorOtp.getId()).orElse(null);
        if (freshOtp != null) {
            twoFactorOtpRepository.delete(freshOtp);
        }
    }

    return isValid;
}

@Override
public void deleteTwoFactorOtp(TwoFactorOTP twoFactorOtp) {
    // ✅ Always fetch a fresh reference before deleting
    TwoFactorOTP freshOtp = twoFactorOtpRepository.findById(twoFactorOtp.getId()).orElse(null);
    if (freshOtp != null) {
        twoFactorOtpRepository.delete(freshOtp);
    }
}

}
