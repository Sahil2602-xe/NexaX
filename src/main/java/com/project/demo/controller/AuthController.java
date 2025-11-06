package com.project.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.demo.config.JwtProvider;
import com.project.demo.model.TwoFactorOTP;
import com.project.demo.model.User;
import com.project.demo.repository.UserRepository;
import com.project.demo.response.AuthResponse;
import com.project.demo.service.CustomUserDetailsService;
import com.project.demo.service.EmailService;
import com.project.demo.service.TwoFactorOtpService;
import com.project.demo.service.WatchlistService;
import com.project.demo.utils.OtpUtils;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TwoFactorOtpService twoFactorOtpService;

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private EmailService emailService;

@PostMapping("/signup")
public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {
    User isEmailExists = userRepository.findByEmail(user.getEmail());
    if (isEmailExists != null) {
        throw new Exception("Email Already Exists");
    }

    User newUser = new User();
    newUser.setEmail(user.getEmail());
    newUser.setPassword(user.getPassword());
    newUser.setFullName(user.getFullName());

    if (user.getEmail().equalsIgnoreCase("admin@gmail.com")) {
        newUser.setRole(com.project.demo.domain.USER_ROLE.ROLE_ADMIN);
    } else {
        newUser.setRole(com.project.demo.domain.USER_ROLE.ROLE_CUSTOMER);
    }

    User savedUser = userRepository.save(newUser);
    watchlistService.createWatchlist(savedUser);

    AuthResponse res = new AuthResponse();
    res.setStatus(true);
    res.setMessage("Registered Successfully. Please login to continue.");
    res.setUser(savedUser);

    return new ResponseEntity<>(res, HttpStatus.CREATED);
}

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {
        String userName = user.getEmail();
        String password = user.getPassword();

        Authentication auth = authenticate(userName, password);
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);
        User authUser = userRepository.findByEmail(userName);

        if (authUser.getTwoFactorAuth() != null && authUser.getTwoFactorAuth().isEnabled()) {
            AuthResponse res = new AuthResponse();
            res.setMessage("Two-Factor Authentication Enabled");
            res.setTwoFactorAuthEnabled(true);

            String otp = OtpUtils.generateOTP();

            TwoFactorOTP oldTwoFactorOtp = twoFactorOtpService.findByUser(authUser.getId());
            if (oldTwoFactorOtp != null) {
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOtp);
            }

            TwoFactorOTP newTwoFactorOtp = twoFactorOtpService.createtwoFactorOTP(authUser, otp, jwt);
            emailService.sendVerificationOtpEmail(userName, otp);

            res.setSession(newTwoFactorOtp.getId());
            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        }

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("Logged In Successfully");
        res.setUser(authUser);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySigninOtp(@PathVariable String otp, @RequestParam String id)
            throws Exception {

        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findById(id);

        if (twoFactorOtpService.verifyTwoFactorOtp(twoFactorOTP, otp)) {
            AuthResponse res = new AuthResponse();
            res.setMessage("Two Factor Authentication Verified");
            res.setTwoFactorAuthEnabled(true);
            res.setJwt(twoFactorOTP.getJwt());
            res.setUser(twoFactorOTP.getUser()); // ✅ return user info
            return new ResponseEntity<>(res, HttpStatus.OK);
        }

        throw new Exception("Invalid OTP");
    }

    private Authentication authenticate(String userName, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }
        if (!password.equals(userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }
}
