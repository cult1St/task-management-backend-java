package com.task_management.first_backend.application.auth.services;

import com.task_management.first_backend.application.auth.dto.AuthResponseDTO;
import com.task_management.first_backend.application.auth.enums.OnboardingSteps;
import com.task_management.first_backend.application.auth.models.OnboardingRequest;
import com.task_management.first_backend.application.auth.repository.OnboardingRepository;
import com.task_management.first_backend.application.users.dto.UserResponseDTO;
import com.task_management.first_backend.application.shared.exceptions.EmailNotVerifiedException;
import com.task_management.first_backend.application.users.models.User;
import com.task_management.first_backend.application.users.repositories.UserRepository;
import com.task_management.first_backend.application.shared.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final OnboardingRepository onboardingRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public void loginTimeStamp(User user) {
        user.setLastLoginAt(new Date());
        userRepository.save(user);
    }

    public AuthResponseDTO loginUser(String email, String password) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            OnboardingRequest draft = onboardingRepository.findFirstByEmailOrderByCreatedAtDesc(email);
            if (draft != null
                    && draft.getOnboardingSteps() == OnboardingSteps.VERIFY_EMAIL
                    && draft.getUser() == null
                    && passwordEncoder.matches(password, draft.getPassword())) {
                throw new EmailNotVerifiedException();
            }
            throw new BadCredentialsException("Invalid username or password");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            String token = jwtUtils.generateToken(authentication.getName());
            User user = (User) authentication.getPrincipal();
            loginTimeStamp(user);
            UserResponseDTO userResponse = new UserResponseDTO(user);
            return new AuthResponseDTO(userResponse, token, userResponse.getOnboarding());
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
