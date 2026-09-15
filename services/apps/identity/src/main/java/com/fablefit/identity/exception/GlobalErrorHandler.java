package com.fablefit.identity.exception;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.rohan.exceptionhandler.ApplicationException;
import com.rohan.exceptionhandler.CommonErrorCode;
import com.rohan.exceptionhandler.ErrorCode;
import com.rohan.exceptionhandler.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalErrorHandler {

        @ExceptionHandler(ApplicationException.class)
        public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex,
                        HttpServletRequest request) {
                ErrorCode errorCode = ex.getErrorCode();
                ErrorResponse errorResponse = new ErrorResponse(errorCode.code(), errorCode.message(),
                                errorCode.status(), request.getRequestURI());

                return ResponseEntity.status(errorCode.status()).body(errorResponse);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                List<Map<String, String>> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                                .map(err -> Map.of("field", err.getField(), "message",
                                                err.getDefaultMessage() != null ? err.getDefaultMessage()
                                                                : "Invalid value"))
                                .toList();

                ErrorResponse errorResponse = ErrorResponse.builder().code(CommonErrorCode.VALIDATION_FAILED.code())
                                .message(CommonErrorCode.VALIDATION_FAILED.message())
                                .status(HttpStatus.BAD_REQUEST.value()).path(request.getRequestURI())
                                .errors(validationErrors).build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        @ExceptionHandler(org.springframework.http.converter.HttpMessageConversionException.class)
        public ResponseEntity<ErrorResponse> handleHttpMessageConversionException(
                        org.springframework.http.converter.HttpMessageConversionException ex,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(CommonErrorCode.VALIDATION_FAILED.code(),
                                "Malformed JSON request body or unreadable property format",
                                HttpStatus.BAD_REQUEST.value(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(AuthErrorCode.ACCESS_DENIED.code(),
                                AuthErrorCode.ACCESS_DENIED.message(), AuthErrorCode.ACCESS_DENIED.status(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(AuthErrorCode.UNAUTHORIZED.code(),
                                ex.getMessage() != null ? ex.getMessage() : AuthErrorCode.UNAUTHORIZED.message(),
                                AuthErrorCode.UNAUTHORIZED.status(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
                log.error("Unhandled exception caught in global error handler", ex);

                ErrorResponse errorResponse = new ErrorResponse(CommonErrorCode.INTERNAL_SERVER_ERROR.code(),
                                CommonErrorCode.INTERNAL_SERVER_ERROR.message(),
                                CommonErrorCode.INTERNAL_SERVER_ERROR.status(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
}