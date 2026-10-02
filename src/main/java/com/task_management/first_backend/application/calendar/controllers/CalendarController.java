package com.task_management.first_backend.application.controllers;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.task_management.first_backend.application.dto.SuccessResponse;
import com.task_management.first_backend.application.dto.calendar.CalendarDTO;
import com.task_management.first_backend.application.dto.calendar.CalendarRequestDTO;
import com.task_management.first_backend.application.dto.calendar.CalendarUpdateRequestDTO;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.services.CalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/calendar/events")
public class CalendarController {
    private final CalendarService calendarService;

    @GetMapping
    public ResponseEntity<SuccessResponse<Page<CalendarDTO>>> index(
            @AuthenticationPrincipal User authUser,
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "limit", defaultValue = "20") int size,
            @RequestParam(name = "page", defaultValue = "1") int page
            ){
        //set correctly params
        size = size > 0 ? size : 20;
        page = page > 0 ? (page - 1) : 0;
        Page<CalendarDTO> events = calendarService.getCalendarEvents(
                authUser, start, end, search, page, size
        );
        return ResponseEntity.ok(
                SuccessResponse.of("Calendar Events fetched Successfully", events)
        );

    }

    @PostMapping
    public ResponseEntity<SuccessResponse<CalendarDTO>> createEvent(
            @AuthenticationPrincipal User authUser,
            @Valid @RequestBody CalendarRequestDTO request
            ){
        CalendarDTO event = calendarService.createCalendarEvent(request, authUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                SuccessResponse.of("Calendar Event created Successfully", event)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SuccessResponse<CalendarDTO>> updateEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal User authUser,
            @Valid @RequestBody CalendarUpdateRequestDTO request
    ){
        CalendarDTO event = calendarService.updateCalendarEvent(id, request, authUser);
        return ResponseEntity.ok(
                        SuccessResponse.of("Calendar Event updated Successfully", event)
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<?>> deleteEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal User authUser
    ){
        calendarService.deleteCalendarEvent(id, authUser);
        return ResponseEntity.ok(
                SuccessResponse.of("Calendar Event deleted Successfully")
        );
    }

}
