package com.task_management.first_backend.application.auth.repository;

import com.task_management.first_backend.application.auth.models.OnboardingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OnboardingRepository extends JpaRepository<OnboardingRequest, Long> {

    OnboardingRequest findFirstByEmailAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
            String email,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    OnboardingRequest findFirstByEmailOrderByCreatedAtDesc(String email);

    Optional<OnboardingRequest> findByUserId(Long userId);
}
