package com.fablefit.identity.controller;

import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fablefit.identity.dto.request.LoginUser;
import com.fablefit.identity.dto.response.AccessTokenCreated;
import com.fablefit.identity.dto.response.UserResponse;
import com.fablefit.identity.entity.User;
import com.fablefit.identity.exception.AuthErrorCode;
import com.fablefit.identity.service.JwtService;
import com.fablefit.identity.service.TenantService;
import com.fablefit.identity.service.UserService;
import com.fablefit.common.response.ApiEnvelope;
import com.fablefit.identity.utils.AuthContext;
import com.fablefit.exceptionhandler.ApplicationException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final TenantService tenantService;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;

    @PostMapping("/login")
    public ResponseEntity<ApiEnvelope<AccessTokenCreated>> login(@RequestBody @Valid LoginUser loginUser,@RequestHeader(name = "X-Tenant-Key") String tenantKey) {
        UUID tenantId = tenantService.getTenantIdFromKey(tenantKey);
        User user = userService.loginUserByTenantId(loginUser.getUserEmail(), loginUser.getUserPassword(),
                tenantId.toString())
                .orElseThrow(() -> new ApplicationException(AuthErrorCode.INVALID_CREDENTIALS));

        String token = jwtService.generateToken(user.getPublicId(),
                Map.of("tenantKey", tenantKey));
        AccessTokenCreated response = AccessTokenCreated.builder().accessToken(token).build();
        return ResponseEntity.ok(ApiEnvelope.success(response));
    }

    @PostMapping("/me")
    public ResponseEntity<ApiEnvelope<UserResponse>> getUserDetails(@AuthenticationPrincipal  AuthContext authContext){
        return ResponseEntity.ok(ApiEnvelope.success(modelMapper.map(authContext.getUser(), UserResponse.class)));
    }
}
