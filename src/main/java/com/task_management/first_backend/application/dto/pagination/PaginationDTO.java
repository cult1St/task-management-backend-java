package com.task_management.first_backend.application.dto.pagination;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PaginationDTO<T> {

    private List<T> data;
    private long total;
    private long perPage;
    private long currentPage;
    private long lastPage;

    // Constructor to convert Spring Page<T> to custom pagination DTO
    public PaginationDTO(Page<T> page) {
        this.data = page.getContent();
        this.total = page.getTotalElements();
        this.perPage = page.getSize();
        this.currentPage = page.getNumber() + 1; // Convert 0-based to 1-based
        this.lastPage = page.getTotalPages();
    }
}
