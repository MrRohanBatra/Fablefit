package com.fablefit.identity.service;

import org.springframework.security.oauth2.jwt.Jwt;

import com.fablefit.identity.entity.User;

public interface JwtService {
    public String generateToken(User user);
    public Jwt decodeToken(String token);
}
