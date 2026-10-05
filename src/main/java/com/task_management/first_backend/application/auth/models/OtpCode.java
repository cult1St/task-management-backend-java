package com.task_management.first_backend.application.auth.models;

import com.task_management.first_backend.application.auth.enums.OtpStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_codes")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtpCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    @Column(nullable = false)
    private String codeHash;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OtpStatus status = OtpStatus.PENDING;
    private LocalDateTime expiryAt;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
