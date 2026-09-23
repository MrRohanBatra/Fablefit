package com.fablefit.exceptionhandler;

import java.util.Collections;
import java.util.Map;

public class ApplicationException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public ApplicationException(ErrorCode errorCode) {
        super(errorCode != null ? errorCode.message() : "Application exception occurred");
        this.errorCode = errorCode;
        this.details = Collections.emptyMap();
    }

    public ApplicationException(ErrorCode errorCode, String customMessage) {
        super(customMessage != null ? customMessage : (errorCode != null ? errorCode.message() : "Application exception occurred"));
        this.errorCode = errorCode;
        this.details = Collections.emptyMap();
    }

    public ApplicationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode != null ? errorCode.message() : "Application exception occurred", cause);
        this.errorCode = errorCode;
        this.details = Collections.emptyMap();
    }

    public ApplicationException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage != null ? customMessage : (errorCode != null ? errorCode.message() : "Application exception occurred"), cause);
        this.errorCode = errorCode;
        this.details = Collections.emptyMap();
    }

    public ApplicationException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode != null ? errorCode.message() : "Application exception occurred");
        this.errorCode = errorCode;
        this.details = details != null ? details : Collections.emptyMap();
    }

    public ApplicationException(ErrorCode errorCode, String customMessage, Map<String, Object> details) {
        super(customMessage != null ? customMessage : (errorCode != null ? errorCode.message() : "Application exception occurred"));
        this.errorCode = errorCode;
        this.details = details != null ? details : Collections.emptyMap();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}