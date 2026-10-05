package com.task_management.first_backend.application.auth.services;

import com.task_management.first_backend.application.auth.dto.OnboardingDTO;
import com.task_management.first_backend.application.auth.dto.RegisterRequestDTO;
import com.task_management.first_backend.application.auth.dto.VerifyUserRequestDTO;
import com.task_management.first_backend.application.auth.enums.OnboardingSteps;
import com.task_management.first_backend.application.auth.helpers.OnboardingMapper;
import com.task_management.first_backend.application.auth.models.OnboardingRequest;
import com.task_management.first_backend.application.auth.models.OtpCode;
import com.task_management.first_backend.application.auth.repository.OnboardingRepository;
import com.task_management.first_backend.application.shared.services.EmailService;
import com.task_management.first_backend.application.users.dto.UserResponseDTO;
import com.task_management.first_backend.application.users.enums.UserRole;
import com.task_management.first_backend.application.users.models.User;
import com.task_management.first_backend.application.users.models.UserSetting;
import com.task_management.first_backend.application.users.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.task_management.first_backend.application.auth.models.OtpCode;
@Service
@RequiredArgsConstructor
public class RegisterService {
    private final UserRepository userRepository;
    private final OnboardingRepository onboardingRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpCodeService otpCodeService;
    private final EmailService emailService;

    @Transactional
    public UserResponseDTO registerUser(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User already exists with this email, please login");
        }

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime startDate = currentDate.minusHours(1);
        if (onboardingRepository.findFirstByEmailAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
                request.getEmail(), startDate, currentDate
        ) != null) {
            throw new IllegalArgumentException("User already exists with this email, please login");
        }

        OnboardingRequest onboardingRequest = OnboardingRequest.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .onboardingSteps(OnboardingSteps.VERIFY_EMAIL)
                .firstProjectCreated(false)
                .build();
        onboardingRepository.save(onboardingRequest);

        String otpCode = otpCodeService.generateOtp(request.getEmail()).getCode();
        emailService.sendVerificationEmail(request.getEmail(), otpCode);
        return generateDraftUserResponse(onboardingRequest);
    }

    @Transactional
    public UserResponseDTO verifyUser(VerifyUserRequestDTO request) {
        User existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser != null) {
            // Idempotent: already verified
            return new UserResponseDTO(existingUser);
        }

        OnboardingRequest draftUser = onboardingRepository.findFirstByEmailOrderByCreatedAtDesc(
                request.getEmail()
        );
        if (draftUser == null) {
            throw new IllegalArgumentException("Invalid or expired verification code.");
        }

        if (draftUser.getOnboardingSteps() != OnboardingSteps.VERIFY_EMAIL) {
            if (draftUser.getUser() != null) {
                return new UserResponseDTO(draftUser.getUser());
            }
            throw new IllegalArgumentException("Invalid or expired verification code.");
        }

        boolean verifyCode = otpCodeService.markAsVerified(request.getCode());
        if (!verifyCode) {
            throw new IllegalArgumentException("Invalid or expired verification code.");
        }

        draftUser.setOnboardingSteps(OnboardingSteps.CREATE_WORKSPACE);

        User newUser = User.builder()
                .email(draftUser.getEmail())
                .fullName(draftUser.getFullName())
                .password(draftUser.getPassword())
                .role(UserRole.USER)
                .build();

        UserSetting userSetting = UserSetting.builder()
                .user(newUser)
                .build();
        newUser.setUserSetting(userSetting);
        userRepository.save(newUser);

        draftUser.setUser(newUser);
        onboardingRepository.save(draftUser);
        newUser.setOnboarding(draftUser);

        return new UserResponseDTO(newUser);
    }

    public boolean resendVerification(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already verified.");
        }

        OnboardingRequest draft = onboardingRepository.findFirstByEmailOrderByCreatedAtDesc(email);
        if (draft == null) {
            // Anti-enumeration: pretend success
            return true;
        }

        var otp = otpCodeService.generateOtp(email, true);
        emailService.sendVerificationEmail(email, otp.getCode());
        return true;
    }

    private UserResponseDTO generateDraftUserResponse(OnboardingRequest onboardingRequest) {
        OnboardingDTO onboarding = OnboardingMapper.from(onboardingRequest);
        return new UserResponseDTO(
                onboardingRequest.getId(),
                onboardingRequest.getFullName(),
                onboardingRequest.getEmail(),
                "Member",
                null,
                null,
                onboarding
        );
    }
}
