package com.task_management.first_backend.application.dto.calendar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CalendarUpdateRequestDTO {
    private String title;

    private String description;
    private String color;

    private LocalDate date;
    private String startTime;
    private String endTime;
}
