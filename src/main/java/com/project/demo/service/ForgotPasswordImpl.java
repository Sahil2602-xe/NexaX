package com.project.demo.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.demo.domain.VerificationType;
import com.project.demo.model.ForgotPasswordToken;
import com.project.demo.model.User;
import com.project.demo.repository.ForgotPasswordRepository;

@Service
public class ForgotPasswordImpl implements ForgotPasswordService {

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Override
    @Transactional
    public ForgotPasswordToken createToken(User user, String id, String otp, VerificationType type, String sendTo) {
        // 🔥 Delete any previous token for this user
        forgotPasswordRepository.deleteByUserId(user.getId());

        // ✅ Create a brand new token
        ForgotPasswordToken token = new ForgotPasswordToken();
        token.setId(UUID.randomUUID().toString());
        token.setOtp(otp);
        token.setVerificationType(type);
        token.setSendTo(sendTo);
        token.setUser(user);

        return forgotPasswordRepository.save(token);
    }

    @Override
    public ForgotPasswordToken findByUser(Long userId) {
        return forgotPasswordRepository.findByUserId(userId);
    }

    @Override
    public ForgotPasswordToken findById(String id) {
        Optional<ForgotPasswordToken> opt = forgotPasswordRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    public void deleteToken(ForgotPasswordToken token) {
        forgotPasswordRepository.delete(token);
    }
}
