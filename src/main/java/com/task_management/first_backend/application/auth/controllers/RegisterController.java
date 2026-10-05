package com.task_management.first_backend.application.auth.controllers;

import com.task_management.first_backend.application.auth.dto.InitialRegisterResponse;
import com.task_management.first_backend.application.auth.dto.RegisterRequestDTO;
import com.task_management.first_backend.application.auth.services.RegisterService;
import com.task_management.first_backend.application.shared.dto.SuccessResponse;
import com.task_management.first_backend.application.users.dto.UserResponseDTO;
import com.task_management.first_backend.application.users.models.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.task_management.first_backend.application.users.models.User;
@RestController
@RequestMapping("/api/v1/auth/register")
@RequiredArgsConstructor
public class RegisterController {
    private final RegisterService registerService;

    @PostMapping()
    public ResponseEntity<SuccessResponse<InitialRegisterResponse>> register(
            @Valid @RequestBody RegisterRequestDTO request
            ){
        UserResponseDTO responseDTO = registerService.registerUser(request);
        return ResponseEntity.ok(
                SuccessResponse.of(
                        "User registered successfully, an otp has been sent to your email please verify",
                        new InitialRegisterResponse(
                                responseDTO.getEmail(), responseDTO.getOnboarding()
                        )
                )
        );
    }
}
