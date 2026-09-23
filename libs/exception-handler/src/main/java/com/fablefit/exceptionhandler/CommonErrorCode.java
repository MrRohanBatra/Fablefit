package com.fablefit.exceptionhandler;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "An unexpected internal server error occurred", 500),
    BAD_REQUEST("BAD_REQUEST", "Invalid request parameters or payload", 400),
    UNAUTHORIZED("UNAUTHORIZED", "Authentication is required to access this resource", 401),
    FORBIDDEN("FORBIDDEN", "You do not have permission to access this resource", 403),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "The requested resource was not found", 404),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "Request HTTP method not supported", 405),
    CONFLICT("CONFLICT", "Resource already exists or conflicting state encountered", 409),
    VALIDATION_FAILED("VALIDATION_FAILED", "Input validation failed", 422),
    TOO_MANY_REQUESTS("TOO_MANY_REQUESTS", "Rate limit exceeded. Please try again later", 429),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", "Service is temporarily unavailable", 503);

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
