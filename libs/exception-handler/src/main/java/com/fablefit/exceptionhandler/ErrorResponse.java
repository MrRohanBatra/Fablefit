package com.fablefit.exceptionhandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    @Builder.Default
    private Instant timestamp = Instant.now();
    private int status;
    private String code;
    private String message;
    private String path;
    private String traceId;

    @Builder.Default
    private List<ValidationError> errors = new ArrayList<>();

    private Map<String, Object> details;

    public ErrorResponse(String code, String message, int status, String path) {
        this.timestamp = Instant.now();
        this.code = code;
        this.message = message;
        this.status = status;
        this.path = path;
        this.errors = new ArrayList<>();
    }
}