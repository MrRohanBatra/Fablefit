package com.fablefit.exceptionhandler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        int status = errorCode != null ? errorCode.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();
        String code = errorCode != null ? errorCode.code() : CommonErrorCode.INTERNAL_SERVER_ERROR.code();
        String message = ex.getMessage() != null ? ex.getMessage() : (errorCode != null ? errorCode.message() : "An unexpected error occurred");

        ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder()
                .status(status)
                .code(code)
                .message(message)
                .path(request.getRequestURI())
                .traceId(getTraceId());

        if (ex.getDetails() != null && !ex.getDetails().isEmpty()) {
            builder.details(ex.getDetails());
        }

        return ResponseEntity.status(status).body(builder.build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ValidationError> validationErrors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(err -> 
            validationErrors.add(ValidationError.builder()
                .field(err.getField())
                .message(err.getDefaultMessage() != null ? err.getDefaultMessage() : "Invalid value")
                .rejectedValue(err.getRejectedValue())
                .build())
        );

        ex.getBindingResult().getGlobalErrors().forEach(err ->
            validationErrors.add(ValidationError.builder()
                .field(err.getObjectName())
                .message(err.getDefaultMessage() != null ? err.getDefaultMessage() : "Invalid value")
                .build())
        );

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(CommonErrorCode.VALIDATION_FAILED.code())
                .message(CommonErrorCode.VALIDATION_FAILED.message())
                .path(request.getRequestURI())
                .traceId(getTraceId())
                .errors(validationErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, HttpMessageConversionException.class})
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(Exception ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(CommonErrorCode.BAD_REQUEST.code())
                .message("Malformed JSON request body or invalid parameter format")
                .path(request.getRequestURI())
                .traceId(getTraceId())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(CommonErrorCode.BAD_REQUEST.code())
                .message(String.format("Required request parameter '%s' of type %s is missing", ex.getParameterName(), ex.getParameterType()))
                .path(request.getRequestURI())
                .traceId(getTraceId())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String paramName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = String.format("Parameter '%s' should be of type '%s'", paramName, requiredType);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(CommonErrorCode.BAD_REQUEST.code())
                .message(message)
                .path(request.getRequestURI())
                .traceId(getTraceId())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .code(CommonErrorCode.METHOD_NOT_ALLOWED.code())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .traceId(getTraceId())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .code(CommonErrorCode.RESOURCE_NOT_FOUND.code())
                .message(CommonErrorCode.RESOURCE_NOT_FOUND.message())
                .path(request.getRequestURI())
                .traceId(getTraceId())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeaderException(MissingRequestHeaderException ex,HttpServletRequest request){
        ErrorResponse errorResponse=ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(CommonErrorCode.BAD_REQUEST.code())
                .message(CommonErrorCode.BAD_REQUEST.message())
                .path(request.getRequestURI())
                .traceId(getTraceId())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception occurred while processing request to {}", request.getRequestURI(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code(CommonErrorCode.INTERNAL_SERVER_ERROR.code())
                .message(CommonErrorCode.INTERNAL_SERVER_ERROR.message())
                .path(request.getRequestURI())
                .traceId(getTraceId())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    protected String getTraceId() {
        String traceId = MDC.get("traceId");
        if (traceId == null || traceId.isBlank()) {
            traceId = MDC.get("trace_id");
        }
        return traceId;
    }
}
