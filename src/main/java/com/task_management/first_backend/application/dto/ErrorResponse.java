package com.task_management.first_backend.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Data
public class ErrorResponse<T> {
    private boolean success;
    private String message;
    private int status;
    private T errors;

    public static <T> ErrorResponse<T> of(String message, T errors){
        return  new ErrorResponse<T>(false, message, 400,  errors);
    }
    public static <T> ErrorResponse<Void> of(String message){
        return  new ErrorResponse<Void>(false, message, 400,  null);
    }
    public static <T> ErrorResponse<T> of(T errors){
        return  new ErrorResponse<T>(false, "An Error Occurred", 400,  errors);
    }
}
