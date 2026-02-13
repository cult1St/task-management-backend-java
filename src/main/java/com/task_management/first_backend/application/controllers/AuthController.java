package com.task_management.first_backend.application.controllers;

import com.task_management.first_backend.application.dto.ErrorResponse;
import com.task_management.first_backend.application.dto.SuccessResponse;
import com.task_management.first_backend.application.dto.UserResponseDTO;
import com.task_management.first_backend.application.dto.auth.AuthResponseDTO;
import com.task_management.first_backend.application.dto.auth.LoginRequestDTO;
import com.task_management.first_backend.application.dto.auth.RegisterRequestDTO;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.services.UserService;
import com.task_management.first_backend.application.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequestDTO requestDTO
            ){
        try{
            User user = userService.registerUser(
                    requestDTO.getFullName(),
                    requestDTO.getEmail(),
                    requestDTO.getPassword()
            );
            UserResponseDTO responseDTO = new UserResponseDTO(user);
            //get auth token
            String token = jwtUtils.generateToken(requestDTO.getEmail());
            //add another hashmap to send a token upon registration
            AuthResponseDTO response = new AuthResponseDTO(responseDTO, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    SuccessResponse.of("User Created Successfully", response)

            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    ErrorResponse.of(e.getMessage())
            );
        }

    }
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDTO requestDTO
    ){
        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDTO.getEmail(), requestDTO.getPassword())
            );
            String token = jwtUtils.generateToken(authentication.getName());
            User user = (User) authentication.getPrincipal();
            AuthResponseDTO response = new AuthResponseDTO(new UserResponseDTO(user), token);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    SuccessResponse.of("User Logged In Successfully", response)

            );
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(
            Authentication authentication
    ){
        if(authentication == null || !authentication.isAuthenticated()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ErrorResponse.of("Unauthorized Request")
            );
        }
        User user = (User) authentication.getPrincipal();
        userService.loginTimeStamp(user);
        return ResponseEntity.ok(
                SuccessResponse.of("User details fetched Successfully", new UserResponseDTO(user))
        );
    }
}
