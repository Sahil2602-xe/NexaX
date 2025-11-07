package com.project.demo.config;

public class JwtConstant {

    // ✅ 64+ character key for HS512 algorithm
    public static final String SECRET_KEY =
            "thisisaverysecureandlongsecretkeyusedforjwtsignaturevalidation12345supersecurekey";

    public static final String JWT_HEADER = "Authorization";
}
