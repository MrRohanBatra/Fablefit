package com.fablefit.identity.config;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

@Configuration
public class RsaConfig {
    
    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        InputStream inputStream=getClass().getClassLoader().getResourceAsStream("certs/public.pem");
        if(inputStream==null){
            throw new IllegalStateException(
                "public key not found"
            );
        }
        String key=new String(
            inputStream.readAllBytes(),
            StandardCharsets.UTF_8
        );

        key=key
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        byte[] decoded=Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec=new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory=KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(pkcs8EncodedKeySpec);   
    }


    @Bean
    public RSAPrivateKey rsaPrivatKey() throws Exception {
        InputStream inputStream=getClass().getClassLoader().getResourceAsStream("certs/private.pem");
        if(inputStream==null){
            throw new IllegalStateException(
                "private key not found"
            );
        }
        String key=new String(
            inputStream.readAllBytes(),
            StandardCharsets.UTF_8
        );

        key=key
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        byte[] decoded=Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec=new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory=KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8EncodedKeySpec);   
    }

    @Bean 
    public JwtEncoder jwtEncoder(RSAPrivateKey privateKey,RSAPublicKey publicKey){
        RSAKey rsaKey=new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(rsaKey)));
    }

    @Bean 
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey){
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}
