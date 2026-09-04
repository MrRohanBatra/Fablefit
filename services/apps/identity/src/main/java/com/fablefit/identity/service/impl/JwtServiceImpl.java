package com.fablefit.identity.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.fablefit.identity.entity.User;
import com.fablefit.identity.service.JwtService;

import lombok.RequiredArgsConstructor;


@Service 
@RequiredArgsConstructor 
public class JwtServiceImpl implements JwtService{
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    @Value("${fablefit.security.jwt-issuer}")
    private String jwtIssuer;
    @Value("${fablefit.security.expiresIn}")
    private Integer expiresIn;

    @Override 
    public String generateToken(User user){
        Instant now=Instant.now();
        JwtClaimsSet claim=JwtClaimsSet.builder()
        .issuer(jwtIssuer)
        .claim("role", user.getRole())
        .subject(user.getPublicId())
        .claim("tenantKey", user.getTenant().getKey())
        .issuedAt(now)
        .expiresAt(now.plus(expiresIn,ChronoUnit.SECONDS))
        .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claim)).getTokenValue();
    }

    @Override
    public Jwt decodeToken(String token) {
        return jwtDecoder.decode(token);
    }    
}
