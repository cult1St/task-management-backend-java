package com.task_management.first_backend.application.exceptions;

import com.task_management.first_backend.application.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex){

        List<Map<String, String>> errors = new ArrayList<>();

        for(FieldError error : ex.getBindingResult().getFieldErrors()){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put(error.getField(), error.getDefaultMessage());
            errors.add(errorMap);
        }

        return ResponseEntity.badRequest().body(
                ErrorResponse.of("Validation Failed. some fields are required", errors)
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse.of(ex.getMessage(), HttpStatus.FORBIDDEN)
        );
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<?> handleAuthException(
            org.springframework.security.core.AuthenticationException ex
    ){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.of(ex.getMessage(), HttpStatus.UNAUTHORIZED)
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex){
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                ErrorResponse.of("Invalid username or password", HttpStatus.UNPROCESSABLE_ENTITY)
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNotFound(NoResourceFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.of("Endpoint not found", HttpStatus.NOT_FOUND)
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex){

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.of("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }
}
