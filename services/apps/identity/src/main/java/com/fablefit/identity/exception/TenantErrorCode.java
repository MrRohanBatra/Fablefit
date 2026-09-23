package com.fablefit.identity.exception;

import com.fablefit.exceptionhandler.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor 
public enum TenantErrorCode implements ErrorCode {
    TENANT_NOT_FOUND("TENANT_NOT_FOUND", "Tenant not found", 404),
    TENANT_ALREADY_EXISTS("TENANT_ALREADY_EXISTS", "Tenant with the specified key or name already exists", 409),
    TENANT_INACTIVE("TENANT_INACTIVE", "Tenant account is inactive", 403),
    TENANT_SUSPENDED("TENANT_SUSPENDED", "Tenant account has been suspended", 403),
    INVALID_TENANT_KEY("INVALID_TENANT_KEY", "Tenant key format is invalid", 400);

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
