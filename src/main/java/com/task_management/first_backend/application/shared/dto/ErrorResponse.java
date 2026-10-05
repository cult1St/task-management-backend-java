package com.task_management.first_backend.application.shared.dto;

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
    private String code;
    private T errors;

    public ErrorResponse(boolean success, String message, int status, T errors) {
        this.success = success;
        this.message = message;
        this.status = status;
        this.errors = errors;
    }

    public static <T> ErrorResponse<T> of(String message, T errors) {
        return new ErrorResponse<>(false, message, 400, errors);
    }

    public static ErrorResponse<Void> of(String message, int status) {
        return new ErrorResponse<>(false, message, status, null);
    }

    public static ErrorResponse<Void> of(String message, int status, String code) {
        ErrorResponse<Void> response = new ErrorResponse<>(false, message, status, null);
        response.setCode(code);
        return response;
    }

    public static <T> ErrorResponse<T> of(T errors) {
        return new ErrorResponse<>(false, "An Error Occurred", 400, errors);
    }
}
