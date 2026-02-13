package com.task_management.first_backend.application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalValidationException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleExceptionValidations(MethodArgumentNotValidException ex){
        List<Map<String, String>> errors = new ArrayList<>();
        for(FieldError error: ex.getFieldErrors()){
            Map<String, String> newMap = new HashMap<>();
            newMap.put(error.getField(), error.getDefaultMessage());
            errors.add(newMap);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Some Fields Are Required");
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
