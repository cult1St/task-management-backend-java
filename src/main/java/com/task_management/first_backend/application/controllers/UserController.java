package com.task_management.first_backend.application.controllers;

import com.task_management.first_backend.application.dto.SuccessResponse;
import com.task_management.first_backend.application.dto.UserResponseDTO;
import com.task_management.first_backend.application.dto.users.UpdateUserRequestDTO;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final Authentication authentication;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> me(){
        User user = (User) authentication.getPrincipal();
        UserResponseDTO responseDTO = new UserResponseDTO(user);
        return ResponseEntity.ok(
                SuccessResponse.of("User Details Fetched Successfully", responseDTO)
        );
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateUser(
            @RequestBody UpdateUserRequestDTO request
    ){
        User authUser = (User) authentication.getPrincipal();
        User updateUser = userService.updateUser(request, authUser.getId());
        return ResponseEntity.ok(
                SuccessResponse.of("User Details Updated Successfully", updateUser)
        );
    }
}
