package com.task_management.first_backend.application.dto.calendar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CalendarRequestDTO {
    @NotBlank(message = "Event Title is required")
    private String title;

    private String description;
    private String color;
    @NotNull
    private LocalDate date;
    private String startTime;
    private String endTime;
}
