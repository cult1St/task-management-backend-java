package com.task_management.first_backend.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OtpResponseDTO {
    private String code;
    private String codeHash;
}
