package com.task_management.first_backend.application.auth.controllers;

import com.task_management.first_backend.application.auth.dto.*;
import com.task_management.first_backend.application.auth.services.AuthService;
import com.task_management.first_backend.application.auth.services.RegisterService;
import com.task_management.first_backend.application.shared.dto.SuccessResponse;
import com.task_management.first_backend.application.users.dto.UserResponseDTO;
import com.task_management.first_backend.application.users.models.User;
import com.task_management.first_backend.application.shared.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RegisterService registerService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        AuthResponseDTO response = authService.loginUser(requestDTO.getEmail(), requestDTO.getPassword());
        return ResponseEntity.ok(
                SuccessResponse.of("User Logged In Successfully", response)
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(
                SuccessResponse.of("User details fetched Successfully", new UserResponseDTO(user))
        );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<SuccessResponse<VerifyUserResponseDTO>> verifyEmail(
            @Valid @RequestBody VerifyUserRequestDTO request
    ) {
        return verifyAndRespond(request);
    }

    @PostMapping("/verify-user")
    public ResponseEntity<SuccessResponse<VerifyUserResponseDTO>> verifyUser(
            @Valid @RequestBody VerifyUserRequestDTO request
    ) {
        return verifyAndRespond(request);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<SuccessResponse<?>> resendVerification(
            @Valid @RequestBody ResendVerificationDTO request
    ) {
        registerService.resendVerification(request.getEmail());
        return ResponseEntity.ok(
                SuccessResponse.of("Verification code sent.", null)
        );
    }

    @DeleteMapping("/logout")
    public ResponseEntity<SuccessResponse<?>> logout() {
        return ResponseEntity.ok(
                SuccessResponse.of("Logged out successfully")
        );
    }

    private ResponseEntity<SuccessResponse<VerifyUserResponseDTO>> verifyAndRespond(
            VerifyUserRequestDTO request
    ) {
        UserResponseDTO verifiedUser = registerService.verifyUser(request);
        String token = jwtUtils.generateToken(verifiedUser.getEmail());
        return ResponseEntity.ok(
                SuccessResponse.of(
                        "User Verified Successfully",
                        new VerifyUserResponseDTO(token, verifiedUser, verifiedUser.getOnboarding())
                )
        );
    }
}
