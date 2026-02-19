package com.task_management.first_backend.application.controllers;

import com.task_management.first_backend.application.dto.SuccessResponse;
import com.task_management.first_backend.application.dto.UserResponseDTO;
import com.task_management.first_backend.application.dto.users.UpdateUserRequestDTO;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<SuccessResponse<UserResponseDTO>> me(
            @AuthenticationPrincipal User authUser
    ) {
        UserResponseDTO responseDTO = new UserResponseDTO(authUser);

        return ResponseEntity.ok(
                SuccessResponse.of("User Details Fetched Successfully", responseDTO)
        );
    }

    @PatchMapping
    public ResponseEntity<SuccessResponse<UserResponseDTO>> updateUser(
            @AuthenticationPrincipal User authUser,
            @Valid @RequestBody UpdateUserRequestDTO request
    ) {
        UserResponseDTO response =
                new UserResponseDTO(userService.updateUser(request, authUser.getId()));

        return ResponseEntity.ok(
                SuccessResponse.of("User Details Updated Successfully", response)
        );
    }
}
