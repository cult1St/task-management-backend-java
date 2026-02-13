package com.task_management.first_backend.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Data
public class SuccessResponse<T> {
    private boolean success;
    private String message;
    private int status;
    private T data;

    public static <T> SuccessResponse<T> of(String message, T data){
        return new SuccessResponse<T>(true, message, 200, data);
    }
    public static <T> SuccessResponse<Void> of(String message){
        return new SuccessResponse<Void>(true, message, 200,  null);
    }
    public static <T> SuccessResponse<T> of(T data){
        return new SuccessResponse<T>(true, "Request Processed Successfully", 200,  data);
    }
}
