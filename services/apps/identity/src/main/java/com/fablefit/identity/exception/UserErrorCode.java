package com.fablefit.identity.exception;

import com.fablefit.exceptionhandler.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found", 404),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "A user with the specified email or username already exists", 409),
    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD", "Current password does not match", 400),
    PASSWORD_TOO_WEAK("PASSWORD_TOO_WEAK", "Password does not meet the security requirements", 400),
    PASSWORD_RESET_TOKEN_EXPIRED("PASSWORD_RESET_TOKEN_EXPIRED", "Password reset token has expired", 400),
    INVALID_ROLE("INVALID_ROLE", "The specified user role is invalid", 400),
    USER_DEACTIVATED("USER_DEACTIVATED", "User account is deactivated", 403),
    UNAUTHORIZED_ROLE_ASSIGNMENT("UNAUTHORIZED_ROLE_ASSIGNMENT","User not allow to set this role",403);

    private final String code;
    private final String message;
    private final int status;

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }

    @Override
    public int status() {
        return this.status;
    }
}
