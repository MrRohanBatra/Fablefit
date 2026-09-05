package com.fablefit.identity.service;

import org.springframework.security.oauth2.jwt.Jwt;

import com.fablefit.identity.entity.User;

import java.util.Map;

public interface JwtService {
    String generateToken(
            String subject,
            Map<String, Object> claims
    );

    boolean validateToken(String token);

    String getSubject(String token);

    String getClaim(String token, String claim);
}
