package com.devision.exceptions;

import java.nio.file.AccessDeniedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice // This tells Spring this class handles exceptions for ALL controllers
public class GlobalExceptionHandler {

    // Handles 404 Not Found errors
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        // Returns 404 NOT FOUND with the exception message
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // Handles 403 Forbidden errors (usually from Spring Security)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        // Returns 403 FORBIDDEN
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied: You do not have permission to perform this action.");
    }

    // You can add a generic handler for unexpected errors (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        // Returns 500 INTERNAL SERVER ERROR (for unexpected issues)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }

    // Handles Spring's configuration-level error (MaxUploadSizeExceededException)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body("File upload failed: File size exceeds the allowed limit set by the server.");
    }

    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<String> handleFileValidat(FileValidationException exc) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("File uploaded too large! Cannot proceed to upload.");
    }
}