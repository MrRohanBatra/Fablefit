package com.fablefit.exceptionhandler;

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
public class ValidationError {
    private String field;
    private String message;
    private Object rejectedValue;

    public ValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
