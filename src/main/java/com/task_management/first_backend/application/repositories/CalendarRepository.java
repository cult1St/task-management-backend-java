package com.task_management.first_backend.application.repositories;

import com.task_management.first_backend.application.models.CalendarEvent;
import com.task_management.first_backend.application.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface CalendarRepository extends JpaRepository<CalendarEvent, Long> {
    @Query("""
    SELECT e FROM CalendarEvent e
    WHERE e.user = :user
      AND e.date >= :startDate
      AND e.date <= :endDate
      AND (
            LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%'))
          )
""")
    Page<CalendarEvent> getEventsByParams(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("search") String search,
            Pageable pageable
    );
}
