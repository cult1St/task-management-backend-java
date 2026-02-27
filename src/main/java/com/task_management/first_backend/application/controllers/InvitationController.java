package com.task_management.first_backend.application.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.task_management.first_backend.application.dto.SuccessResponse;
import com.task_management.first_backend.application.dto.invitations.InvitationDTO;
import com.task_management.first_backend.application.dto.invitations.InvitationRespondRequestDTO;
import com.task_management.first_backend.application.dto.projects.ProjectDTO;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequestMapping("/api/v1/invitations")
public class InvitationController {
    private final ProjectService projectService;
    @GetMapping("/received")
    public ResponseEntity<SuccessResponse<Page<InvitationDTO>>> getReceivedInvitations(
            @AuthenticationPrincipal User authUser,
            @RequestParam(name = "limit", defaultValue = "20") int size,
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        //set correctly params
        size = size > 0 ? size : 20;
        page = page > 0 ? (page - 1) : 0;
        Page<InvitationDTO> invitations = projectService
                .getReceivedInvitations(authUser, page, size);
        return ResponseEntity.ok(
                SuccessResponse.of("Received Invitations Fetched Successfully", invitations)
        );
    }

    @GetMapping("/sent")
    public ResponseEntity<SuccessResponse<Page<InvitationDTO>>> getSentInvitations(
            @AuthenticationPrincipal User authUser,
            @RequestParam(name = "limit", defaultValue = "20") int size,
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        //set correctly params
        size = size > 0 ? size : 20;
        page = page > 0 ? (page - 1) : 0;
        Page<InvitationDTO> invitations = projectService
                .getSentInvitations(authUser, page, size);
        return ResponseEntity.ok(
                SuccessResponse.of("Sent Invitations Fetched Successfully", invitations)
        );
    }
    @PatchMapping("/{invitationId}/respond")
    public ResponseEntity<SuccessResponse<InvitationDTO>> saveInvitationResponse(
            @AuthenticationPrincipal User authUser,
            @PathVariable Long invitationId,
            @Valid @RequestBody InvitationRespondRequestDTO requestDTO
            ){
        InvitationDTO invitation = projectService
                .respondToInvitationRequest(invitationId, authUser, requestDTO);
        return ResponseEntity.ok(
                SuccessResponse.of("Invitation Response Successfully", invitation)
        );
    }
}
