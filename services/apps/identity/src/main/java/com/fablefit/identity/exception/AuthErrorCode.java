package com.fablefit.identity.exception;

import com.rohan.exceptionhandler.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid username or password", 401),
    UNAUTHORIZED("UNAUTHORIZED", "Authentication is required to access this resource", 401),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Authentication token has expired", 401),
    TOKEN_INVALID("TOKEN_INVALID", "Authentication token is invalid or malformed", 401),
    TOKEN_MISSING("TOKEN_MISSING", "Bearer authentication token is missing", 401),
    ACCESS_DENIED("ACCESS_DENIED", "You do not have permission to perform this action", 403),
    ACCOUNT_LOCKED("ACCOUNT_LOCKED", "Your account has been locked. Please contact support", 403),
    ACCOUNT_DISABLED("ACCOUNT_DISABLED", "Your account is disabled", 403);

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
