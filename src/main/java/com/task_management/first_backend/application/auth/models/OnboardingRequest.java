package com.task_management.first_backend.application.auth.models;

import com.task_management.first_backend.application.auth.enums.OnboardingSteps;
import com.task_management.first_backend.application.users.models.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "onboarding_requests")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String email;
    private String phone;
    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OnboardingSteps onboardingSteps = OnboardingSteps.VERIFY_EMAIL;

    @Builder.Default
    private boolean firstProjectCreated = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
