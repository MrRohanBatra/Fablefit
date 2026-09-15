package com.fablefit.identity.service.impl;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import com.fablefit.identity.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${fablefit.security.jwt-issuer}")
    private String jwtIssuer;

    @Value("${fablefit.security.jwt-expiresIn}")
    private Integer expiresIn;

    @Override
    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .subject(subject)
                .claims(map -> {
                    if (claims != null) {
                        map.putAll(claims);
                    }
                })
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            decodeToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    @Override
    public String getSubject(String token) {
        return decodeToken(token).getSubject();
    }

    @Override
    public String getClaim(String token, String claim) {
        return decodeToken(token).getClaimAsString(claim);
    }

    private Jwt decodeToken(String token) throws JwtException {
        return jwtDecoder.decode(token);
    }
}
