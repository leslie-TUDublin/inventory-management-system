package com.lesya.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

// EN: Handles application errors and converts errors into structured API responses.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // EN: Handles cases when the requested resource does not exist.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                exception.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    // EN: Handles cases when a unique resource value already exists.
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateResource(
            DuplicateResourceException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                exception.getMessage(),
                HttpStatus.CONFLICT.value()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    // EN: Handles authentication failures caused by incorrect credentials.
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED.value()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    // EN: Handles VALIDATION ERRORS from request DTOs.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException exception
    ) {
        // EN: Collects all validation messages into one string.
        String errorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ApiErrorResponse response = new ApiErrorResponse(
                errorMessage,
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}