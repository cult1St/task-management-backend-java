package com.task_management.first_backend.application.dto.calendar;

import com.task_management.first_backend.application.helpers.DateHelper;
import com.task_management.first_backend.application.models.CalendarEvent;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@Data
public class CalendarDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String color;

    public CalendarDTO(CalendarEvent event){
        setId(event.getId());
        setTitle(event.getTitle());
        setDescription(event.getDescription());
        setDate(DateHelper.dateToLocale(event.getDate()));
        setStartTime(event.getStartTime().toLocalTime());
        setEndTime(event.getEndTime().toLocalTime());
        setColor(event.getColour());
    }
}
