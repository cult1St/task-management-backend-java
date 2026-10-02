package com.task_management.first_backend.application.services;


import com.task_management.first_backend.application.dto.calendar.CalendarDTO;
import com.task_management.first_backend.application.dto.calendar.CalendarRequestDTO;
import com.task_management.first_backend.application.dto.calendar.CalendarUpdateRequestDTO;
import com.task_management.first_backend.application.models.CalendarEvent;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.repositories.CalendarRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final CalendarRepository repository;

    public Page<CalendarDTO> getCalendarEvents(
            User user,
            String startDateString,
            String endDateString,
            String search,
            int page, int size
    ){
        Pageable pageable = PageRequest.of(page, size);

        //parse strings dates to locale date
        LocalDate startDate = LocalDate.parse(startDateString);
        LocalDate endDate = LocalDate.parse(endDateString);

        Page<CalendarEvent> events = repository.getEventsByParams(
                user,
                startDate,
                endDate,
                search,
                pageable
        );
        return events.map(this::mapToDTO);

    }

    public CalendarDTO createCalendarEvent(CalendarRequestDTO request, User user){
        LocalDateTime startTime = LocalDateTime.of(
                request.getDate(), LocalTime.parse(request.getStartTime())
        );
        LocalDateTime endTime = LocalDateTime.of(
                request.getDate(), LocalTime.parse(request.getEndTime())
        );

        CalendarEvent event = CalendarEvent.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .colour(request.getColor())
                .date(request.getDate())
                .startTime(startTime)
                .endTime(endTime)
                .build();
        CalendarEvent savedEvent = repository.save(event);
        return new CalendarDTO(savedEvent);
    }

    public CalendarDTO updateCalendarEvent(Long id, CalendarUpdateRequestDTO request, User user) {

        CalendarEvent event = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Calendar Event does not exist"));

        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }

        if (request.getColor() != null) {
            event.setColour(request.getColor());
        }

        if (request.getDate() != null) {
            event.setDate(request.getDate());
        }

        if (request.getStartTime() != null) {
            LocalDateTime startTime = LocalDateTime.of(
                    request.getDate() != null ? request.getDate() : event.getDate(),
                    LocalTime.parse(request.getStartTime())
            );
            event.setStartTime(startTime);
        }

        if (request.getEndTime() != null) {
            LocalDateTime endTime = LocalDateTime.of(
                    request.getDate() != null ? request.getDate() : event.getDate(),
                    LocalTime.parse(request.getEndTime())
            );
            event.setEndTime(endTime);
        }

        CalendarEvent updatedEvent = repository.save(event);

        return new CalendarDTO(updatedEvent);
    }

    public void deleteCalendarEvent(Long id, User user) {

        CalendarEvent event = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Calendar event does not exist"));

        // Ensure the user owns the event
        if (!event.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this event");
        }

        repository.delete(event);
    }

    private CalendarDTO mapToDTO(CalendarEvent event){
        return new CalendarDTO(event);
    }

}
