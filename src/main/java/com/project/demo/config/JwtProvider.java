package com.project.demo.config;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;

/**
 * JwtProvider - generates tokens and exposes helpers used by the rest of app.
 * Compatible with JJWT 0.12+ and matches JwtTokenValidator implementation.
 */
public class JwtProvider {

    // Use the same secret key constant used by JwtTokenValidator
    private static final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    // Algorithm constant for explicitness
    private static final MacAlgorithm algorithm = Jwts.SIG.HS512;

    public static String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);

        String primaryRole = authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_CUSTOMER");

        return Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .claim("email", auth.getName())
                .claim("authorities", roles)
                .claim("role", primaryRole)
                .signWith(key, algorithm)
                .compact();
    }

    /**
     * Returns the email claim extracted from the token (accepts "Bearer ..." or raw token).
     */
    public static String getEmailFromToken(@NonNull String token) {
        String raw = token.startsWith("Bearer ") ? token.substring(7) : token;
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(raw)
                .getPayload();
        return claims.get("email", String.class);
    }

    private static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auth = new HashSet<>();
        for (GrantedAuthority ga : authorities) {
            auth.add(ga.getAuthority());
        }
        return String.join(",", auth);
    }
}
