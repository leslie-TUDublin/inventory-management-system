package com.lesya.inventory.exception;

// EN: Standard response returned by the API when an error occurs.
public record ApiErrorResponse(String message, int status) {
}