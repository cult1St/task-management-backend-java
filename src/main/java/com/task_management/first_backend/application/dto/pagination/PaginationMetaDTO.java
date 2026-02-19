package com.task_management.first_backend.application.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginationMetaDTO {
    private long total;
    private long perPage;
    private long currentPage;
    private long lastPage;
}
