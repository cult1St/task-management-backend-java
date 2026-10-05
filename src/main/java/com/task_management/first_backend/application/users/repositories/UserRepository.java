package com.task_management.first_backend.application.users.repositories;

import com.task_management.first_backend.application.users.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.onboarding WHERE u.email = :email")
    User findByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    Page<User> findByIdNotAndFullNameContainingIgnoreCaseOrIdNotAndEmailContainingIgnoreCase(
            Long id1,
            String fullName,
            Long id2,
            String email,
            Pageable pageable
    );
}
