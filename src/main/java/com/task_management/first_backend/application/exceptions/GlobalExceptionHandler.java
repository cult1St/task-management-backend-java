package com.task_management.first_backend.application.exceptions;

import com.task_management.first_backend.application.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity <?> handleAccessDenied(AccessDeniedException exception){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse.of(exception.getMessage(), HttpStatus.FORBIDDEN)
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(
            AuthenticationException exception
    ){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.of(exception.getMessage(), HttpStatus.UNAUTHORIZED)
        );
    }
    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.of(ex.getMessage(), HttpStatus.BAD_REQUEST)
        );
    }
}
