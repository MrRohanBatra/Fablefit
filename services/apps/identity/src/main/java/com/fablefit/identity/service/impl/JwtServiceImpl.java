package com.fablefit.identity.service.impl;

import java.time.Instant;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import com.fablefit.identity.service.JwtService;

import lombok.RequiredArgsConstructor;


@Service 
@RequiredArgsConstructor 
public class JwtServiceImpl implements JwtService{
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    @Value("${fablefit.security.jwt-issuer}")
    private String jwtIssuer;
    @Value("${fablefit.security.jwt-expiresIn}")
    private Integer expiresIn;
    /**
     * @param subject
     * @param claims
     * @return
     */
    @Override
    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now= Instant.now();
        JwtClaimsSet jwtClaimsSet= JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .subject(subject)
                .claims(map->{
                    if(claims!=null){
                        map.putAll(claims);
                    }
                })
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    /**
     * @param token
     * @return
     */
    @Override
    public boolean validateToken(String token) {
        try {
            decodeToken(token);
            return true;
        }
        catch (JwtException e) {
            return false;
        }
    }

    /**
     * @param token
     * @return
     */
    @Override
    public String getSubject(String token) {
        return decodeToken(token).getSubject();
    }

    /**
     * @param token
     * @param claim
     * @return
     */
    @Override
    public String getClaim(String token, String claim) {
        return decodeToken(token).getClaimAsString(claim);
    }

    private Jwt decodeToken(String token) throws JwtException {
        return jwtDecoder.decode(token);
    }


}
