package com.fablefit.identity.service;

import java.util.Map;

public interface JwtService {
    String generateToken(String subject, Map<String, Object> claims);

    boolean validateToken(String token);

    String getSubject(String token);

    String getClaim(String token, String claim);
}
