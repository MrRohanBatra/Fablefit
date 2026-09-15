package com.fablefit.identity.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fablefit.identity.dto.request.LoginUser;
import com.fablefit.identity.dto.response.AccessTokenCreated;
import com.fablefit.identity.entity.User;
import com.fablefit.identity.exception.AuthErrorCode;
import com.fablefit.identity.service.JwtService;
import com.fablefit.identity.service.TenantService;
import com.fablefit.identity.service.UserService;
import com.fablefit.identity.utils.ApiEnvelope;
import com.rohan.exceptionhandler.ApplicationException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final TenantService tenantService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<ApiEnvelope<AccessTokenCreated>> login(@RequestBody @Valid LoginUser loginUser) {
        UUID tenantId = tenantService.getTenantIdFromKey(loginUser.getTenantKey());
        User user = userService.loginUserByTenantId(loginUser.getUserEmail(), loginUser.getUserPassword(),
                tenantId.toString())
                .orElseThrow(() -> new ApplicationException(AuthErrorCode.INVALID_CREDENTIALS));

        String token = jwtService.generateToken(user.getPublicId(),
                Map.of("role", user.getRole().name(), "tenantKey", loginUser.getTenantKey()));
        AccessTokenCreated response = AccessTokenCreated.builder().accessToken(token).build();
        return ResponseEntity.ok(ApiEnvelope.success(response));
    }
}
