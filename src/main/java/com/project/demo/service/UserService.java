package com.project.demo.service;

import com.project.demo.domain.VerificationType;
import com.project.demo.model.User;

public interface UserService {

    User findUserProfileByJwt(String jwt) throws Exception;

    User findUserByEmail(String email) throws Exception;

    User findUserById(Long userId) throws Exception;

    User enableTwoFactorAuthentication(
            VerificationType verificationType,
            String sendTo,
            User user);

    User updatePassword(User user, String newPassword);

    // ✅ added missing save() method
    User save(User user);
}
