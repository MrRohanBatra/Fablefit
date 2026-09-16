package com.fablefit.identity.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fablefit.identity.dto.request.CreateUser;
import com.fablefit.identity.dto.request.UpdateUser;
import com.fablefit.identity.dto.request.UpdateUserRole;
import com.fablefit.identity.dto.response.UserResponse;
import com.fablefit.identity.service.UserService;
import com.fablefit.identity.utils.ApiEnvelope;
import com.fablefit.identity.utils.AuthContext;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('WRITE')")
    @PostMapping
    public ResponseEntity<ApiEnvelope<UserResponse>> createUser(
            @Valid @RequestBody CreateUser createUser,
            @AuthenticationPrincipal AuthContext authContext) {
        UserResponse response = userService.createUser(createUser, authContext.getTenant());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiEnvelope.success(response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/role")
    public ResponseEntity<ApiEnvelope<UserResponse>> updateUserRole(
            @PathVariable("userId") String userId,
            @Valid @RequestBody UpdateUserRole updateUserRole,
            @AuthenticationPrincipal AuthContext authContext) {
        UserResponse response = userService.updateUserRole(updateUserRole, userId, authContext.getTenant().getId());
        return ResponseEntity.ok(ApiEnvelope.success(response));
    }

    @PreAuthorize("hasRole('WRITE')")
    @PutMapping("/{userId}/update")
    public ResponseEntity<ApiEnvelope<UserResponse>> updateUser(
            @PathVariable("userId") String userId,
            @Valid @RequestBody UpdateUser updateUser,
            @AuthenticationPrincipal AuthContext authContext) {
        UserResponse response = userService.updateUser(updateUser, userId, authContext.getTenant().getId());
        return ResponseEntity.ok(ApiEnvelope.success(response));
    }
    @GetMapping({"","/"})
    public ResponseEntity<ApiEnvelope<List<UserResponse>>> getAllUsers(@AuthenticationPrincipal AuthContext authContext){
        List<UserResponse> userResponses=userService.getAllUsers(authContext.getTenant().getId());
        return ResponseEntity.ok().body(ApiEnvelope.success(userResponses));
    }
}