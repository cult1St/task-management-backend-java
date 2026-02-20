package com.task_management.first_backend.application.controllers;

import com.task_management.first_backend.application.dto.SuccessResponse;
import com.task_management.first_backend.application.dto.UserResponseDTO;
import com.task_management.first_backend.application.dto.users.UpdateUserRequestDTO;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<UserResponseDTO>> me(
            @AuthenticationPrincipal User authUser
    ) {
        UserResponseDTO responseDTO = new UserResponseDTO(authUser);

        return ResponseEntity.ok(
                SuccessResponse.of("User Details Fetched Successfully", responseDTO)
        );
    }

    @PatchMapping("/me")
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
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<Page<UserResponseDTO>>> searchUsers(
            @AuthenticationPrincipal User authUser,
            @RequestParam(name = "q") String query,
            @RequestParam(name = "limit", defaultValue = "20") int size,
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        //set correctly params
        size = size > 0 ? size : 20;
        page = page > 0 ? (page - 1) : 0;
       Page<UserResponseDTO> response = userService.searchUsers(query, authUser, page, size);
        return ResponseEntity.ok(
                SuccessResponse.of("Users Searched Successfully", response)
        );
    }
}
