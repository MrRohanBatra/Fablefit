package com.fablefit.identity.controller;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fablefit.identity.dto.request.LoginUser;
import com.fablefit.identity.dto.response.AccessTokenCreated;
import com.fablefit.identity.entity.User;
import com.fablefit.identity.service.JwtService;
import com.fablefit.identity.service.TenantService;
import com.fablefit.identity.service.UserService;
import com.fablefit.identity.utils.ApiEnvelope;

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
        Optional<User> user = userService.loginUserByTenantId(loginUser.getUserEmail(), loginUser.getUserPassword(),
                tenantId.toString());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiEnvelope.error("INVALID_CREDENTIALS", "Invalid email or password"));
        }
        User u = user.get();
        String token = jwtService.generateToken(u.getPublicId(),
                Map.of("role", u.getRole().name(), "tenantKey", loginUser.getTenantKey()));
        AccessTokenCreated response = AccessTokenCreated.builder().accessToken(token).build();
        return ResponseEntity.ok(ApiEnvelope.success(response));
    }
}
