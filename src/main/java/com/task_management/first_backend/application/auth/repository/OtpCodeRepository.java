package com.task_management.first_backend.application.auth.repository;

import com.task_management.first_backend.application.auth.enums.OtpStatus;
import com.task_management.first_backend.application.auth.models.OtpCode;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    @Query("""
            SELECT o FROM OtpCode o
            WHERE o.codeHash = :hash
            AND o.status = :status
            AND o.expiryAt > :currentTime
            """)
    OtpCode getValidOtp(
            @Param("hash") String hash,
            @Param("currentTime") LocalDateTime currentTime,
            @Param("status") OtpStatus status
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
            UPDATE OtpCode o SET o.status = :status
            WHERE o.email = :email
            AND o.status <> :status
            """)
    int updateOthersToUsed(
            @Param("email") String email,
            @Param("status") OtpStatus status
    );

    Optional<OtpCode> findFirstByEmailOrderByCreatedAtDesc(String email);
}
