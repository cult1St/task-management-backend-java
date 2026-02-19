package com.task_management.first_backend.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.task_management.first_backend.application.dto.pagination.PaginationDTO;
import com.task_management.first_backend.application.dto.pagination.PaginationMetaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Data
public class SuccessResponse<T> {
    private boolean success;
    private String message;
    private int status;
    private T data;
    private PaginationMetaDTO meta;

    public SuccessResponse(boolean success, String message, int status, T data){
        setSuccess(success);
        setMessage(message);
        setStatus(status);

        if (data instanceof Page<?> pageData) {  // runtime type check
            PaginationDTO paginationDTO = new PaginationDTO(pageData);
            setData((T) paginationDTO.getData()); // cast to T
            setMeta(new PaginationMetaDTO(
                    paginationDTO.getTotal(),
                    paginationDTO.getPerPage(),
                    paginationDTO.getCurrentPage(),
                    paginationDTO.getLastPage()
            ));
        } else {
            setData(data);
        }
    }


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
