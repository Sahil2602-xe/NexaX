package com.project.demo.response;
import com.project.demo.model.User;

import lombok.Data;

@Data
public class AuthResponse {

    private String jwt;
    private boolean status;
    private String message;
    private boolean isTwoFactorAuthEnabled;
    private String session;
    private User user; 
}
