package com.task_management.first_backend.application.repositories;

import com.task_management.first_backend.application.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String email);
    public boolean existsByEmail(String email);
}
