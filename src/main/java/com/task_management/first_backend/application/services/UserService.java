package com.task_management.first_backend.application.services;

import com.task_management.first_backend.application.dto.auth.LoginRequestDTO;
import com.task_management.first_backend.application.enums.UserRole;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Date;

@Service
@AllArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(String fullName, String email, String password) throws Exception{
        //check if a user exists with this email
        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("User with this email already exists");
        }
        User createdUser = User
                .builder()
                .fullName(fullName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(UserRole.USER)
                .build();
        return userRepository.save(createdUser);
    }

    public void loginTimeStamp(User user){
        user.setLastLoginAt(new Date());
        userRepository.save(user);
    }

}
