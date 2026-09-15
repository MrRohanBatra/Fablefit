package com.fablefit.identity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fablefit.identity.dto.request.CreateUser;
import com.fablefit.identity.dto.response.UserResponse;
import com.fablefit.identity.service.UserService;
import com.fablefit.identity.utils.ApiEnvelope;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('WRITE')")
    @PostMapping
    public ResponseEntity<ApiEnvelope<UserResponse>> createUser(@Valid @RequestBody CreateUser createUser){
        UserResponse u = userService.createUser(createUser);
        return ResponseEntity.ok().body(ApiEnvelope.success(u));
    }
}